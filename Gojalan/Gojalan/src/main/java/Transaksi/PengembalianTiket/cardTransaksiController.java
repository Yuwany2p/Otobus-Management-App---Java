package Transaksi.PengembalianTiket;

import Master.Tiket.Tiket;
import Transaksi.PembelianTiket.PembelianTiket;
import Transaksi.PembelianTiket.PembelianTiketController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;

public class cardTransaksiController {

    @FXML
    private Label labelTanggalPembalian;

    @FXML
    private Label lblAsal;

    @FXML
    private Label lblJumlahKursi;

    @FXML
    private Label lblKeberangkatan;

    @FXML
    private Label lblNamaPembeli;

    @FXML
    private Label lblTujuan;

    @FXML
    private AnchorPane btnDetail;

    @FXML
    private Label lblNo;

    private PembelianTiket pembelian;
    private AnchorPane rootPane;

    public void setRootPane(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }

    public void setData(PembelianTiket pembelian) {
        lblNo.setText(String.valueOf(pembelian.getNO()));
        this.pembelian = pembelian;
        lblNamaPembeli.setText(pembelian.getNama());
        lblKeberangkatan.setText(pembelian.getKeberangkatan());
        lblAsal.setText(pembelian.getRute_Asal());
        lblTujuan.setText(pembelian.getRute_Tujuan());
        labelTanggalPembalian.setText(pembelian.getTgl_Pembelian());
        lblJumlahKursi.setText(String.valueOf(pembelian.getBanyak_kursi()) + " Kursi");
        System.out.println(pembelian.getID_PembelianTiket());
    }

    @FXML
    public void handleLihatDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transaksi/Detail.fxml"));
            AnchorPane popupContent = loader.load();

            DetailController controller = loader.getController();// Jika controller detail butuh rootPane
            System.out.println(pembelian.getID_PembelianTiket());
            controller.setPembelianTiket(pembelian);


            // Buat overlay gelap
            AnchorPane overlay = new AnchorPane();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            overlay.prefWidthProperty().bind(rootPane.widthProperty());
            overlay.prefHeightProperty().bind(rootPane.heightProperty());


            controller.setRootPane(rootPane);
            // Posisikan di tengah
            popupContent.setLayoutX((rootPane.getWidth() - popupContent.getPrefWidth()) / 2);
            popupContent.setLayoutY((rootPane.getHeight() - popupContent.getPrefHeight()) / 2);

            overlay.getChildren().add(popupContent);
            rootPane.getChildren().add(overlay);

            // Biarkan controller tahu overlay jika perlu
            controller.setOverlay(overlay);

            // Tutup saat klik di luar popup
            overlay.setOnMouseClicked(event -> rootPane.getChildren().remove(overlay));
            popupContent.setOnMouseClicked(event -> event.consume());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}