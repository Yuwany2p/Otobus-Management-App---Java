package Transaksi.PengembalianTiket;

import DBConnect.DBConnect;
import Master.Tiket.Tiket;
import Transaksi.PembelianTiket.PembelianTiket;
import Transaksi.PembelianTiket.layoutKursiController;
import Transaksi.PembelianTiket.tiketController;
import Transaksi.PembelianTiket.tiketSelectedController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PembatalanTiketController implements Initializable {

    @FXML
    private Button btnRefresh;

    @FXML
    private Label lblTotalRow;

    @FXML
    private Pane paneCari;

    @FXML
    private TextField textCari;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vbTransaksi;

    DBConnect db = new DBConnect();
    private String usr;
    List<PembelianTiket> ptList = new ArrayList<>();


    public void setUsername(String usrnm){
        this.usr = usrnm;
    }

    public void loadTransaksi() {
        vbTransaksi.getChildren().clear(); // kosongkan dulu konten sebelumnya

        List<PembelianTiket> daftarPembelian = getAllPembelian(); // method buatanmu untuk ambil data dari DB

        try {
            for (PembelianTiket pembelianTiket : daftarPembelian) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transaksi/transaksiCard.fxml"));
                Node cardNode = loader.load();

                // ambil controller-nya
                cardTransaksiController controller = loader.getController();
                controller.setData(pembelianTiket);

                controller.setRootPane(rootPane);

                // tambahkan ke VBox
                vbTransaksi.getChildren().add(cardNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal memuat tiket: " + e.getMessage());
        }
        lblTotalRow.setText(daftarPembelian.size() + " Transaksi Ditemukan");
    }

    public List<PembelianTiket> getAllPembelian() {
        int i = 1;
        try {
            String query = "{call sp_getListPembelian(?, ?, ?, ?)}";
            CallableStatement stmt = db.conn.prepareCall(query);

            // Set parameter: search (bisa null), status, sortColumn, sortOrder
            stmt.setString(1, null); // Tidak ada pencarian khusus
            stmt.setNull(2, java.sql.Types.INTEGER); // Status bisa null (semua data)
            stmt.setString(3, "ID_Pembelian_Tiket"); // Kolom sorting
            stmt.setString(4, "ASC"); // Urutan sorting

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ptList.add(new PembelianTiket(
                        i,
                        rs.getString("ID_Pembelian_Tiket"),
                        rs.getString("NIK_User"),
                        rs.getString("ID_Tiket"),
                        rs.getString("Keberangkatan"),
                        rs.getString("Kepulangan"),
                        rs.getString("Rute_Asal"),
                        rs.getString("Rute_Tujuan"),
                        rs.getString("Tgl_Pembelian"),
                        rs.getString("No_Telepon"),
                        rs.getString("Alamat"),
                        rs.getString("Nama"),
                        rs.getDouble("Total_Harga"),
                        rs.getInt("Jumlah_Kursi")
                ));
                i++;
            }

            rs.close();
            stmt.close();


        } catch (Exception ex) {
            System.out.println("Terjadi error saat load data Pembelian: " + ex.getMessage());
            ex.printStackTrace();
        }
        return ptList;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTransaksi();
        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            ptList.clear(); // bersihkan isi tabel sebelumnya
            cariPembelian(newVal);
        });
    }


    @FXML
    public void cariPembelian(String kataKunci) {
        try {
            ptList.clear();

            String query = "{call sp_getListPembelian(?, ?, ?, ?)}";
            CallableStatement stmt = db.conn.prepareCall(query);

            stmt.setString(1, kataKunci); // pencarian teks
            stmt.setNull(2, java.sql.Types.INTEGER); // status (semua)
            stmt.setString(3, "ID_Pembelian_Tiket"); // urut berdasarkan kolom ini
            stmt.setString(4, "ASC"); // urutan

            ResultSet rs = stmt.executeQuery();
            int i = 1;
            while (rs.next()) {
                ptList.add(new PembelianTiket(
                        i,
                        rs.getString("ID_Pembelian_Tiket"),
                        rs.getString("NIK_User"),
                        rs.getString("ID_Tiket"),
                        rs.getString("Keberangkatan"),
                        rs.getString("Kepulangan"),
                        rs.getString("Rute_Asal"),
                        rs.getString("Rute_Tujuan"),
                        rs.getString("Tgl_Pembelian"),
                        rs.getString("No_Telepon"),
                        rs.getString("Alamat"),
                        rs.getString("Nama"),
                        rs.getDouble("Total_Harga"),
                        rs.getInt("Jumlah_Kursi")
                ));
                i++;
            }

            rs.close();
            stmt.close();

            // Tampilkan ke tampilan
            vbTransaksi.getChildren().clear();
            for (PembelianTiket pt : ptList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transaksi/transaksiCard.fxml"));
                Node cardNode = loader.load();

                cardTransaksiController controller = loader.getController();
                controller.setData(pt);
                controller.setRootPane(rootPane);

                vbTransaksi.getChildren().add(cardNode);
            }

            lblTotalRow.setText(ptList.size() + " Transaksi Ditemukan");

        } catch (Exception e) {
            System.out.println("Gagal mencari Pembelian: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
