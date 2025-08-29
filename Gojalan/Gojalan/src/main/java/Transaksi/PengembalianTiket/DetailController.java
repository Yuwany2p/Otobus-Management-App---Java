package Transaksi.PengembalianTiket;

import DBConnect.DBConnect;
import Master.Bus.Bus;
import Master.Setting.Setting;
import Transaksi.PembelianTiket.DetailPembelianTiket;
import Transaksi.PembelianTiket.PembelianTiket;
import Transaksi.PembelianTiket.PembelianTiketController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetailController implements Initializable {

    @FXML
    private Button btnBatal;

    @FXML
    private TableColumn<DetailPembelianTiket, Void> clmAksi;

    @FXML
    private TableColumn<DetailPembelianTiket, String> clmIDKursi;

    @FXML
    private TableColumn<DetailPembelianTiket, String> clmNoKursi;

    @FXML
    private TableColumn<DetailPembelianTiket, Integer> clmStatus;

    @FXML
    private TableColumn<DetailPembelianTiket, String> clmTanggal;

    @FXML
    private TableColumn<DetailPembelianTiket, Double> clmTotalPengembalian;

    @FXML
    private TableColumn<DetailPembelianTiket, Integer> clmNo;

    @FXML
    private TableView<DetailPembelianTiket> tblDetail;

    private ObservableList<DetailPembelianTiket> detailList = FXCollections.observableArrayList();
    DBConnect db = new DBConnect();

    private PembelianTiket pembelianTiket;

    private AnchorPane rootPane;

    private AnchorPane overlay;

    private List<String> listNoKursi = new ArrayList<>();
    private List<String> listIdKursi = new ArrayList<>();

    public void setPembelianTiket(PembelianTiket pembelianTiket) {
        this.pembelianTiket = pembelianTiket;
        System.out.println(pembelianTiket.getID_PembelianTiket());
    }

    public void setOverlay(AnchorPane overlay) {
        this.overlay = overlay;
    }

    public void setRootPane(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }

    @FXML
    private void onBatalClick() {
        if (rootPane != null && overlay != null) {
            rootPane.getChildren().remove(overlay);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clmNo.setCellValueFactory(new PropertyValueFactory<>("No"));
        clmIDKursi.setCellValueFactory(new PropertyValueFactory<>("idKursi"));
        clmNoKursi.setCellValueFactory(new PropertyValueFactory<>("noKursi"));
        clmStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        clmTotalPengembalian.setCellValueFactory(new PropertyValueFactory<>("totalPengembalian"));
        clmTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalPembatalan"));


        clmNo.setStyle("-fx-alignment: CENTER; -fx-font-size: 15px;");
        clmIDKursi.setStyle("-fx-alignment: CENTER; -fx-font-size: 15px;");
        clmNoKursi.setStyle("-fx-alignment: CENTER; -fx-font-size: 15px;");
        clmStatus.setStyle("-fx-alignment: CENTER; -fx-font-size: 15px;");
        clmTotalPengembalian.setStyle("-fx-alignment: CENTER_RIGHT; -fx-font-size: 15px;");
        clmTanggal.setStyle("-fx-alignment: CENTER; -fx-font-size: 15px;");
        clmAksi.setStyle("-fx-alignment: CENTER; -fx-font-size: 15px;");

        tblDetail.setFixedCellSize(50);  // Baris tabel lebih tinggi
    }

    @FXML
    public void loadDetailPembelian() {

        String idTransaksi = pembelianTiket.getID_PembelianTiket();
        System.out.println("ini dalam loadDetailPembelian: " + idTransaksi);
        detailList.clear(); // ← ini WAJIB agar tidak menumpuk data lama
        tblDetail.getItems().clear(); // ← opsional, jika mau pastikan tabel juga kosong
        List<Pair<String, String>> kursiList = new ArrayList<>();

        try {
            // Ambil daftar ID_Kursi dan No_Kursi untuk transaksi tertentu
            String query = "SELECT dp.ID_Kursi, k.No_Kursi " +
                    "FROM detailPembelianTiket dp " +
                    "JOIN Kursi k ON dp.ID_Kursi = k.ID_Kursi " +
                    "WHERE dp.ID_Pembelian_Tiket = ?";

            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setString(1, idTransaksi);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String idKursi = rs.getString("ID_Kursi");
                String noKursi = rs.getString("No_Kursi");
                kursiList.add(new Pair<>(idKursi, noKursi));
            }
            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        int No = 1;
        // Panggil SP untuk setiap kursi
        for (Pair<String, String> kursi : kursiList) {
            try {
                String sql = "{ call sp_getListDetailPembelian(?, ?, ?) }";
                CallableStatement stmt = db.conn.prepareCall(sql);
                stmt.setString(1, kursi.getKey());      // ID_Kursi
                stmt.setString(2, kursi.getValue());    // No_Kursi
                stmt.setString(3, idTransaksi);         // ID_Pembelian_Tiket

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    detailList.add(new DetailPembelianTiket(
                            No,
                            rs.getString("ID_Pembelian_Tiket"),
                            rs.getString("ID_Kursi"),
                            rs.getInt("Status"),
                            rs.getString("No_Kursi"),
                            rs.getDouble("Persen_Pengembalian"),
                            rs.getDouble("Total_Pengembalian"),
                            rs.getString("Tanggal_Pembatalan"),
                            rs.getString("ID_Tiket"),
                            rs.getDouble("Harga_Tiket"))
                    );
                }

                rs.close();
                stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            No++;
        }
        tblDetail.setItems(detailList); // Tampilkan data setelah selesai semua

        addAksiButtonToTable();
        setupStatusColumn();
    }

    private void addAksiButtonToTable() {
        clmAksi.setCellFactory(param -> new javafx.scene.control.TableCell<>() {
            private final Button btnBatal = new Button("Batal");

            {
                btnBatal.setStyle(
                        "-fx-background-color: red; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 5; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 6px 12px;"
                );
                btnBatal.setOnAction(event -> {
                    DetailPembelianTiket data = getTableView().getItems().get(getIndex());
                    System.out.println("ini IDTransaksinya : " + data.getId_Tiket());
                    System.out.println("Ini Harga Tiket : " + data.getHarga());
                    handleBatalAction(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    DetailPembelianTiket currentData = getTableView().getItems().get(getIndex());
                    btnBatal.setDisable(currentData.getStatus() == 0); // Disable kalau status = 0 (merah)
                    setGraphic(btnBatal);
                }

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnBatal);
                }
            }
        });
    }

    @FXML
    private void setupStatusColumn() {
        clmStatus.setCellFactory(column -> new TableCell<DetailPembelianTiket, Integer>() {
            @Override
            protected void updateItem(Integer status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String icon = status == 1 ? "✅" : "❌";

                    javafx.scene.control.Label label = new javafx.scene.control.Label(icon);
                    label.setStyle("-fx-font-size: 18px; -fx-alignment: center;");

                    setGraphic(label);
                    setText(null);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }


    private void handleBatalAction(DetailPembelianTiket detail) {
        // Konfirmasi pembatalan
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Pembatalan");
        alert.setHeaderText("Apakah Anda yakin ingin membatalkan kursi ini?");
        alert.initOwner(btnBatal.getScene().getWindow());
        alert.setContentText("Kursi: " + detail.getNoKursi() + "\nID Kursi: " + detail.getIdKursi());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String sql = "{ call sp_Pengembalian(?, ?, ?, ?) }";
                CallableStatement stmt = db.conn.prepareCall(sql);
                stmt.setString(1, detail.getIdPembelianTiket());
                stmt.setString(2, detail.getIdKursi());
                stmt.setString(3, detail.getNoKursi());
                stmt.setDouble(4, detail.getHarga());

                stmt.executeUpdate();
                stmt.close();

                // Update tampilan lokal setelah batal
                detail.setStatus(0);  // Set status ke batal (merah)
                tblDetail.refresh();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Pembatalan Berhasil");
                successAlert.setHeaderText(null);
                successAlert.initOwner(btnBatal.getScene().getWindow());
                successAlert.setContentText("Kursi berhasil dibatalkan.");
                successAlert.showAndWait();

            } catch (SQLException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Kesalahan");
                errorAlert.setHeaderText("Gagal Membatalkan Kursi");
                errorAlert.initOwner(btnBatal.getScene().getWindow());
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }


}
