package Transaksi.PembelianTiket;

import DBConnect.DBConnect;
import Master.Kursi.Kursi;
import Master.Tiket.Tiket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class layoutKursiController implements Initializable {

    @FXML
    private Button btnBatal;

    @FXML
    private ComboBox<Kursi> cbKursiA;

    @FXML
    private ComboBox<Kursi> cbKursiB;

    @FXML
    private ImageView imageLayout;

    @FXML
    private Label lblKonfirmasi;

    @FXML
    private Label labelKursi;

    @FXML
    private Label lblTambah;

    private DBConnect db = new DBConnect();
    private ObservableList<Kursi> listKursi = FXCollections.observableArrayList();
    private List<String> listNomorKursi = new ArrayList<>();

    private Tiket tiket; // tiket dipilih, dikirim dari Card
    private AnchorPane overlay;
    private AnchorPane rootPane;
    private VBox vbSelectedTiket;

    private PembelianTiketController pembelianTiketController;

    private Button btnTambah;

    public void setBtnTambah(Button btnTambah) {
        this.btnTambah = btnTambah;
    }


    public void setPembelianTiketController(PembelianTiketController controller) {
        this.pembelianTiketController = controller;
    }

    public void setOverlay(AnchorPane overlay) {
        this.overlay = overlay;
    }

    public void setRootPane(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }

    public void setVbSelectedTiket(VBox vb) {
        this.vbSelectedTiket = vb;
    }

    public void setTiket(Tiket tiket) {
        this.tiket = tiket;
        loadNoKursi(); // load saat tiket dikirim
        setLayoutKursi();
    }


    @FXML
    private void tambahEntered(){
        lblTambah.setStyle("-fx-text-fill: gray");
    }

    @FXML
    private void tambahExited(){
        lblTambah.setStyle("");
    }

    @FXML
    private void konfirmasiEntered(){
        lblKonfirmasi.setStyle("-fx-text-fill: gray");
    }

    @FXML
    private void konfirmasiExited(){
        lblKonfirmasi.setStyle("");
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Berhasil");
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (btnBatal.getScene() != null && btnBatal.getScene().getWindow() != null) {
            alert.initOwner(btnBatal.getScene().getWindow()); // Supaya alert tidak fullscreen
        }

        alert.initModality(Modality.WINDOW_MODAL); // Modal terhadap window pemilik saja
        alert.showAndWait();
    }

    private void showFailedMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Ganti jadi ERROR biar lebih informatif
        alert.setTitle("Gagal");
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (btnBatal.getScene() != null && btnBatal.getScene().getWindow() != null) {
            alert.initOwner(btnBatal.getScene().getWindow());
        }

        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }


    @FXML
    private void onKonfirmasi() {
        if (labelKursi.getText().equals("")) {
            showFailedMessage("Pilih Kursi Sebelum Konfirmasi!");
            return;
        }

        try {
            // Tutup overlay jika ada
            if (rootPane != null && overlay != null) {
                rootPane.getChildren().remove(overlay);
            }

            // Load FXML tampilan kartu tiket yang sudah dipilih
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transaksi/CardSelected.fxml"));
            Node node = loader.load();

            // Ambil controllernya
            tiketSelectedController controller = loader.getController();
            pembelianTiketController.setListNomorKursi(listNomorKursi);

            controller.setPembelianTiketController(pembelianTiketController);
            controller.setData(tiket, listKursi);

            // Tambahkan node ke VBox di tampilan utama
            if (vbSelectedTiket != null) {
                vbSelectedTiket.getChildren().add(node);
            }

            if (pembelianTiketController != null) {
                pembelianTiketController.disableAllBtnTambah();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBatalClick() {
        if (rootPane != null && overlay != null) {
            rootPane.getChildren().remove(overlay);
        }
    }


    @FXML
    private void onTambahKursi() {
        Kursi selectedA = cbKursiA.getValue();
        Kursi selectedB = cbKursiB.getValue();

        if (selectedA == null && selectedB == null) return;

        if (selectedA != null && !listKursi.contains(selectedA)) {
            listNomorKursi.add(selectedA.getID_Kursi());
            listKursi.add(selectedA);
        }

        if (selectedB != null && !listKursi.contains(selectedB)) {
            listNomorKursi.add(selectedB.getID_Kursi());
            listKursi.add(selectedB);
        }

        updateLabelKursi();

        cbKursiA.getSelectionModel().clearSelection();
        cbKursiB.getSelectionModel().clearSelection();

        cbKursiA.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Kursi item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Pilih Kursi A" : item.getNoKursi());
            }
        });

        cbKursiB.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Kursi item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Pilih Kursi B" : item.getNoKursi());
            }
        });
    }


    public void setLayoutKursi() {
        if (tiket == null) return;

        int kapasitas = tiket.getKapasitas(); // Ambil kapasitas dari objek tiket

        String imagePath;
        if (kapasitas == 40) {
            imagePath = layoutKursi40();
        } else if (kapasitas == 50) {
            imagePath = layoutKursi50();
        } else {
            imagePath = null;
        }

        if (imagePath != null) {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageLayout.setImage(image);
        }
    }



    public void loadNoKursi() {
        if (tiket == null) return;

        String idBus = tiket.getID_Bus();
        String idTiket = tiket.getID_Tiket();
        System.out.println("Ini ID Bus yang ada di tiket : " + idBus);
        System.out.println("Ini ID Tiket yang ada : " + idTiket);

        String query = "SELECT k.ID_Kursi, k.No_Kursi, k.ID_Bus\n" +
                "FROM Kursi k\n" +
                "WHERE k.ID_Bus = ?\n" +
                "  AND k.Status = 1\n" +
                "  AND k.ID_Kursi NOT IN (\n" +
                "      SELECT dpt.ID_Kursi\n" +
                "      FROM detailPembelianTiket dpt\n" +
                "\t  join trsPembelian Trs on Trs.ID_Pembelian_Tiket = dpt.ID_Pembelian_Tiket\n" +
                "\t  join Tiket T on T.ID_Tiket = Trs.ID_Tiket\n" +
                "      WHERE T.ID_Tiket = ?);";
        try {
            PreparedStatement ps = db.conn.prepareStatement(query);
            ps.setString(1, idBus);
            ps.setString(2,idTiket);

            ResultSet rs = ps.executeQuery();

            ObservableList<Kursi> listA = FXCollections.observableArrayList();
            ObservableList<Kursi> listB = FXCollections.observableArrayList();

            while (rs.next()) {
                Kursi kursi = new Kursi(
                        rs.getString("ID_Kursi"),
                        rs.getString("No_Kursi"),
                        rs.getString("ID_Bus")
                );

                if (kursi.getNoKursi().startsWith("A")) {
                    listA.add(kursi);
                } else if (kursi.getNoKursi().startsWith("B")) {
                    listB.add(kursi);
                }
            }

            cbKursiA.setItems(listA);
            cbKursiB.setItems(listB);

            // Tampilkan No_Kursi di ComboBox (bukan objek toString)
            cbKursiA.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Kursi item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNoKursi());
                }
            });
            cbKursiA.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Kursi item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNoKursi());
                }
            });

            cbKursiB.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Kursi item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNoKursi());
                }
            });
            cbKursiB.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Kursi item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNoKursi());
                }
            });

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLabelKursi() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listKursi.size(); i++) {
            sb.append(listKursi.get(i).getNoKursi());
            if (i < listKursi.size() - 1) {
                sb.append(", ");
            }
        }
        labelKursi.setText(sb.toString());
    }



    public String layoutKursi40(){
        String path40 = "/Images/LayoutKursiKapasitas40.png";
        return path40;
    }

    public String layoutKursi50(){
        String path50 = "/Images/LayoutKursiKapasitas50.png";
        return path50;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbKursiA.setPromptText("Pilih Kursi A");
        cbKursiB.setPromptText("Pilih Kursi B");
    }
}
