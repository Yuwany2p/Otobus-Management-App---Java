package Transaksi.PembelianTiket;

import Master.Kursi.Kursi;
import Master.Tiket.Tiket;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class tiketSelectedController {

    @FXML
    private Label lblDari;

    @FXML
    private Label lblHargaTiket;

    @FXML
    private Label lblKe;

    @FXML
    private Label lblKeberangkatan;

    @FXML
    private Label lblKuantitas;

    private Tiket tiket;

    private PembelianTiketController pembelianTiketController;

    public void setPembelianTiketController(PembelianTiketController controller) {
        this.pembelianTiketController = controller;
    }


    public void setData(Tiket tiket, ObservableList<Kursi> listKursi) {
        if (tiket != null) {
            lblDari.setText(tiket.getRute_Asal()); // asal rute
            lblKe.setText(tiket.getRute_Tujuan());     // tujuan rute
            lblKeberangkatan.setText(tiket.getKeberangkatan().toString()); // atau format tanggal
            lblHargaTiket.setText(String.format("Rp %,f", tiket.getHarga())); // format rupiah
            System.out.println(String.format("Rp %,.0f", tiket.getHarga()));
        }

        if (listKursi != null && !listKursi.isEmpty()) {
            lblKuantitas.setText(String.valueOf(listKursi.size()) + " Kursi"); // jumlah kursi
        } else {
            lblKuantitas.setText("0");
        }


        String hargaString = lblHargaTiket.getText(); // contoh: "Rp 50,000.000000"

        // 1. Hapus "Rp" dan spasi
        hargaString = hargaString.replace("Rp", "").trim();

        // 2. Hapus koma ribuan dan desimal
        hargaString = hargaString.replace(",", "").split("\\.")[0];

        // 3. Parse ke int
        int hargaTiket = Integer.parseInt(hargaString);

        String kuantitasText = lblKuantitas.getText();  // contoh: "2X" atau "2 Tiket"

        // Ambil angka saja dengan regex
        kuantitasText = kuantitasText.replaceAll("[^0-9]", "");
        int jumlahTiket = Integer.parseInt(kuantitasText);// hasil: "2"

        pembelianTiketController.updateTotalHarga(hargaTiket, jumlahTiket);
        pembelianTiketController.loadTiket();

    }



}
