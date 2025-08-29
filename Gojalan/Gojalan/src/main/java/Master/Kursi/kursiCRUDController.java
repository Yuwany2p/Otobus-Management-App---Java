package Master.Kursi;

import DBConnect.DBConnect;
import Master.Bus.Bus;
import Master.Setting.Setting;
import Master.User.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class kursiCRUDController implements Initializable {
    @FXML
    private TableView<Kursi> tblKursi;

    @FXML
    private TableColumn<Kursi, Integer> clmNo;

    @FXML
    private TableColumn<Kursi, String> clmNoKursi;

    @FXML
    private TableColumn<Kursi, String>  clmNoPolisi;

    @FXML
    private TableColumn<Kursi, String>  clmTersedia;

    @FXML
    private TableColumn<Kursi, Void> clmAksi;

    @FXML
    private TextField textCari;

    @FXML
    private TextField textID;

    @FXML
    private TextField textNoKursi;

    @FXML
    private ComboBox <Bus>cbNoPolisi;

    @FXML
    private Label lblValidasi;

    public String usr;

    ObservableList<Kursi> krslist = FXCollections.observableArrayList();
    DBConnect db = new DBConnect();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clmNoKursi.setCellValueFactory(new PropertyValueFactory<>("noKursi"));
        clmNoPolisi.setCellValueFactory(new PropertyValueFactory<>("namaBus"));
        clmTersedia.setCellValueFactory(new PropertyValueFactory<>("tersedia"));
        clmNo.setCellValueFactory(new PropertyValueFactory<>("No"));
        textID.setEditable(false);

        clmNoKursi.setStyle("-fx-alignment: CENTER;");
        clmNoPolisi.setStyle("-fx-alignment: CENTER;");
        clmTersedia.setStyle("-fx-alignment: CENTER;");
        clmNo.setStyle("-fx-alignment: CENTER;");
        clmAksi.setStyle("-fx-alignment: CENTER;");

        addButtonToTable();

        tblKursi.setItems(krslist);
        tblKursi.setStyle("-fx-font-size: 20px");

        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            krslist.clear(); // bersihkan isi tabel sebelumnya
            cariKursi(newVal);
        });

        textNoKursi.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        setMaxLength(textNoKursi, 3);
        tblKursi.refresh();
        mengambilID();
        loadKursi();
        loadComboBox();

    }

    private void setMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > maxLength) {
                textField.setText(oldVal); // atau pakai substring kalau mau auto potong
                // textField.setText(newVal.substring(0, maxLength));
            }
        });
    }

    @FXML
    public void loadKursi(){
        try{
            String query = "{call sp_getListKursi()}";
            CallableStatement stmt = db.conn.prepareCall(query);
            db.result = stmt.executeQuery();
            while(db.result.next()){
                krslist.add(new Kursi(
                        db.result.getInt("No"),
                        db.result.getString("ID_Kursi"),
                        db.result.getString("No Polisi"),
                        db.result.getString("No Kursi"),
                        db.result.getInt("Tersedia"),
                        db.result.getString("ID_Bus")
                ));
            }
            db.stat.close();
            db.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data Kursi  : " + ex);
        }
    }

    public void loadComboBox() {
        try {
            String query = "SELECT Nomor_Polisi,ID_Bus FROM Bus WHERE Status = 1 AND Tersedia = 1;";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            ObservableList<Bus> list = FXCollections.observableArrayList();

            while (db.result.next()) {
                list.add(new Bus(db.result.getString("ID_Bus"), db.result.getString("Nomor_Polisi")));
            }

            cbNoPolisi.setItems(list);

            db.stat.close();
            db.result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void refresh(){
        tblKursi.getItems().clear();
        loadKursi();
        mengambilID();
    }

    public void setUsername(String usrnm){
        this.usr = usrnm;
    }

    public void mengambilID() {
        String ID = "";
        try {
            DBConnect db = new DBConnect(); // Asumsikan ini class koneksi milikmu
            LocalDate today = LocalDate.now();
            String year = String.valueOf(today.getYear());
            String month = String.format("%02d", today.getMonthValue());
            String day = String.format("%02d", today.getDayOfMonth());
            String tanggalHariIni = year + month + day;

            String query = "SELECT ID_Kursi FROM Kursi ORDER BY ID_Kursi DESC;";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            if (!db.result.next()) {
                // Tidak ada data sama sekali
                ID = "KR" + tanggalHariIni + "-0001";
            } else {
                String lastId = db.result.getString("ID_Kursi");
                String lastTanggal = lastId.substring(2, 10); // Ambil bagian YYYYMMDD dari ID lama

                if (!lastTanggal.equals(tanggalHariIni)) {
                    // Jika tanggal berbeda, mulai dari 0001 lagi
                    ID = "KR" + tanggalHariIni + "-0001";
                } else {
                    String angka = lastId.split("-")[1]; // Ambil angka setelah tanda '-'
                    int prefix = Integer.parseInt(angka);
                    ID = "KR" + tanggalHariIni + "-" + String.format("%04d", prefix + 1);
                }
            }

        } catch (SQLException e) {
            System.out.println("GAGAL: " + e.getMessage());
        }
        textID.setText(ID);
        textID.setEditable(false);
    }

    @FXML
    public void clear(){
        lblValidasi.setVisible(false);
        textID.setText("");
        cbNoPolisi.getSelectionModel().clearSelection();
        textNoKursi.setText("");
        mengambilID();
    }

    @FXML
    public void onBtnSimpanClick() {
        DBConnect db = new DBConnect();
        String ID = textID.getText();
        int Status;
        int tersedia = 1;
        String NoKursi = textNoKursi.getText();
        String id = "";


        Bus selectedSetting = cbNoPolisi.getSelectionModel().getSelectedItem();
        if (selectedSetting != null) {
            id = selectedSetting.getID_Bus();
        }


        try {
            String query = "{call Sp_InsertKursi( ?, ?, ?, ?, ?)}";

            CallableStatement cstmt = db.conn.prepareCall(query);
            cstmt.setString(1, ID);
            cstmt.setString(2, id);
            cstmt.setString(3, NoKursi);
            cstmt.setInt(4, tersedia);
            cstmt.setString(5, usr);

            cstmt.execute(); // gunakan execute() jika prosedur tidak mengembalikan ResultSet

            cstmt.close();
            db.conn.close();


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Data berhasil ditambahkan!");
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
            refresh();
            clear();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : " + ex.getMessage());
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
        }

    }

    @FXML
    public void cariKursi(String kataKunci) {
        try {
            krslist.clear();
            String query = "{call sp_getListKursi(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, kataKunci); // parameter dari textfield

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                krslist.add(new Kursi(
                        rs.getInt("No"),
                        rs.getString("ID_Kursi"),
                        rs.getString("No Polisi"),
                        rs.getString("No Kursi"),
                        rs.getInt("Tersedia"),
                        rs.getString("Id_Bus")
                ));
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal mencari Kursi: " + e.getMessage());
        }
    }


    private void addButtonToTable() {
        Callback<TableColumn<Kursi, Void>, TableCell<Kursi, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Kursi, Void> call(final TableColumn<Kursi, Void> param) {
                final TableCell<Kursi, Void> cell = new TableCell<>() {

                    private final Button btnEdit = new Button("📝");
                    private final Button btnHapus = new Button("❌");
                    private final HBox hbox = new HBox(5); // jarak antar tombol

                    {
                        btnEdit.setStyle(
                                "-fx-font-size: 17px;" +
                                        "-fx-min-width: 36px;" +
                                        "-fx-min-height: 36px;" +
                                        "-fx-background-color: #4CAF50;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-background-radius: 6px;"
                        );
                        btnEdit.setFont(Font.font("Segoe UI Emoji", 14));
                        btnEdit.getStyleClass().addAll("button", "btn-edit");

                        btnHapus.setStyle(
                                "-fx-font-size: 17px;" +
                                        "-fx-min-width: 36px;" +
                                        "-fx-min-height: 36px;" +
                                        "-fx-background-color: #F44336;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-background-radius: 6px;"
                        );                        btnHapus.setFont(Font.font("Segoe UI Emoji", 14));
                        btnHapus.getStyleClass().addAll("button", "btn-hapus");
                        hbox.getChildren().addAll(btnEdit, btnHapus);

                        btnEdit.setOnAction((event) -> {
                            Kursi krs = getTableView().getItems().get(getIndex());
                            // Ambil data dan isi ke form
                            textID.setText(krs.getID_Kursi());
                            textNoKursi.setText(krs.getNoKursi());
                            System.out.println(krs.toString());

                            ObservableList<Bus> daftar = cbNoPolisi.getItems();
                            for (int i = 0; i < daftar.size(); i++) {
                                if (daftar.get(i).getID_Bus() == krs.getID_Bus()) {
                                    cbNoPolisi.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        });

                        btnHapus.setOnAction((event) -> {
                            Kursi krs = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi");
                            alert.setHeaderText("Yakin ingin menghapus?");
                            alert.setContentText("Data " + krs.getNoKursi() + " akan dihapus.");

                            // Dapatkan stage utama dari button
                            Window window = btnHapus.getScene().getWindow();
                            alert.initOwner(window);
                            alert.initModality(Modality.WINDOW_MODAL); // tidak ambil alih fokus penuh
                            alert.initStyle(StageStyle.UTILITY); // opsional agar tidak terlalu "popup besar"

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    hapusKursi(krs.getID_Kursi());
                                    refresh();
                                }
                            });
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
                return cell;
            }
        };

        clmAksi.setCellFactory(cellFactory);
    }

    private void hapusKursi(String ID) {
        try {
            String query = "{call Sp_delete_kursi(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, ID);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal menghapus: " + e.getMessage());
        }
    }


}
