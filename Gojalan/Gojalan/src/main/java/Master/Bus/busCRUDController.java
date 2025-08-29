package Master.Bus;

import DBConnect.DBConnect;
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

import javax.swing.*;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class busCRUDController implements Initializable {

    @FXML
    private TableView<Bus> tblSetting;

    @FXML
    private TableColumn<Bus, Integer> clmNo;

    @FXML
    private TableColumn<Bus, String> clmID;

    @FXML
    private TableColumn<Bus, String> clmJenisBus;

    @FXML
    private TableColumn<Bus, String> clmNoPolisi;

    @FXML
    private TableColumn<Bus, String> clmKapasitas;

    @FXML
    private TableColumn<Bus, Void> clmAksi;

    @FXML
    private TextField textCari;

    @FXML
    private ComboBox<Setting> cbJenis;

    @FXML
    private TextField textID;

    @FXML
    private TextField textKapasitas;

    @FXML
    private TextField textNoPolisi;

    @FXML
    private Label lblValidasi;

    public String usr;

    ObservableList<Bus> uslist = FXCollections.observableArrayList();
    DBConnect db = new DBConnect();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clmID.setCellValueFactory(new PropertyValueFactory<>("ID_Bus"));
        clmJenisBus.setCellValueFactory(new PropertyValueFactory<>("TipeBus"));
        clmNoPolisi.setCellValueFactory(new PropertyValueFactory<>("NoPolisi"));
        clmKapasitas.setCellValueFactory(new PropertyValueFactory<>("Kapasitas"));
        clmNo.setCellValueFactory(new PropertyValueFactory<>("No"));

        clmID.setStyle("-fx-alignment: CENTER;");
        clmJenisBus.setStyle("-fx-alignment: CENTER;");
        clmNoPolisi.setStyle("-fx-alignment: CENTER;");
        clmKapasitas.setStyle("-fx-alignment: CENTER-RIGHT;");
        clmNo.setStyle("-fx-alignment: CENTER;");


        addButtonToTable();
        tblSetting.setItems(uslist);
        tblSetting.setStyle("-fx-font-size: 20px");

        //menambah
        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            uslist.clear(); // bersihkan isi tabel sebelumnya
            cariBus(newVal);
        });



        textKapasitas.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));

        textNoPolisi.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            uslist.clear();
            cariBus(newVal.toLowerCase()); // ← ini penting
        });


        tblSetting.refresh();

        loadBus();
        loadComboBox();
        mengambilID();
        setMaxLength(textNoPolisi, 20);
        setMaxLength(textKapasitas, 2);
    }

    @FXML
    public void cariBus(String kataKunci) {

        int stts = 1;


        try {
            uslist.clear();
            String query = "{call sp_getListBus(?,?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, kataKunci.toLowerCase()); // parameter dari textfield
            stmt.setInt(2, stts);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                uslist.add(new Bus(
                        rs.getInt("No"),
                        rs.getString("ID_Bus"),
                        rs.getInt("ID_Setting"),
                        rs.getString("Nomor_Polisi"),
                        rs.getInt("Tersedia"),
                        rs.getInt("Kapasitas"),
                        rs.getString("Tipe Bus")));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal mencari setting: " + e.getMessage());
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<Bus, Void>, TableCell<Bus, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Bus, Void> call(final TableColumn<Bus, Void> param) {
                final TableCell<Bus, Void> cell = new TableCell<>() {

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
                        );
                        btnHapus.setFont(Font.font("Segoe UI Emoji", 14));
                        btnHapus.getStyleClass().addAll("button", "btn-hapus");
                        hbox.getChildren().addAll(btnEdit, btnHapus);

                        btnEdit.setOnAction((event) -> {
                            Bus bus = getTableView().getItems().get(getIndex());
                            // Ambil data dan isi ke form
                            textID.setText(bus.getID_Bus());
                            textKapasitas.setText(String.valueOf(bus.getKapasitas()));
                            textNoPolisi.setText(bus.getNoPolisi());

                            ObservableList<Setting> daftar = cbJenis.getItems();
                            for (int i = 0; i < daftar.size(); i++) {
                                if (daftar.get(i).getId_setting() == bus.getID_Setting()) {
                                    cbJenis.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        });

                        btnHapus.setOnAction((event) -> {
                            Bus bus = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi");
                            alert.setHeaderText("Yakin ingin menghapus?");
                            alert.setContentText("Data " + bus.getID_Bus() + " akan dihapus.");

                            // Dapatkan stage utama dari button
                            Window window = btnHapus.getScene().getWindow();
                            alert.initOwner(window);
                            alert.initModality(Modality.WINDOW_MODAL); // tidak ambil alih fokus penuh
                            alert.initStyle(StageStyle.UTILITY); // opsional agar tidak terlalu "popup besar"

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    hapusBus(bus.getID_Bus());
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

    public void loadBus(){
        try{
            String query = "{call sp_getListBus()}";
            CallableStatement stmt = db.conn.prepareCall(query);
            db.result = stmt.executeQuery();
            while(db.result.next()){
                uslist.add(new Bus(
                        db.result.getInt("No"),
                        db.result.getString("ID_Bus"),
                        db.result.getInt("ID_Setting"),
                        db.result.getString("Nomor_Polisi"),
                        db.result.getInt("Tersedia"),
                        db.result.getInt("Kapasitas"),
                        db.result.getString("Tipe Bus")));

            }
            db.stat.close();
            db.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data User  : " + ex);
        }
    }

    public void loadComboBox() {
        try {
            String query = "SELECT Value,ID_Setting  FROM Setting WHERE Nama ='Tipe Bus'";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            ObservableList<Setting> list = FXCollections.observableArrayList();

            while (db.result.next()) {
                list.add(new Setting(db.result.getInt("ID_Setting"), db.result.getString("Value")));
            }

            cbJenis.setItems(list);

            db.stat.close();
            db.result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hapusBus(String ID) {
        try {
            String query = "{call sp_delete_bus(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, ID);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal menghapus: " + e.getMessage());
        }
    }

    @FXML
    public void refresh(){
        tblSetting.getItems().clear();
        loadBus();
    }




    //menambah dan update
    public void setUsername(String usrnm){
        this.usr = usrnm;
    }

    @FXML
    public void onBtnSimpanClick() {
        DBConnect db = new DBConnect();
        int Jenis = 0;
        String ID = textID.getText();
        Setting selectedSetting = cbJenis.getSelectionModel().getSelectedItem();

        if (selectedSetting != null) {
            Jenis = selectedSetting.getId_setting();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : Jenis Bus Tidak Boleh Kosong");
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
        }

        String NoPolisi = textNoPolisi.getText().toUpperCase();

        int Kapasitas = 0 ;
        if (!textKapasitas.getText().equals("")) {
            Kapasitas = Integer.parseInt(textKapasitas.getText());
        }
        try {
            String query = "{call Sp_InsertBus(?, ?, ?, ?, ?, ?)}";

            CallableStatement cstmt = db.conn.prepareCall(query);
            cstmt.setString(1, ID);
            cstmt.setString(2, NoPolisi);
            cstmt.setInt(3, 1);
            cstmt.setInt(4, Kapasitas);
            cstmt.setString(5, usr);
            cstmt.setInt(6, Jenis);

            cstmt.execute();
            cstmt.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Data berhasil ditambahkan!");
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
            clear();
            refresh();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : " + ex.getMessage());
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
        }

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

            String query = "SELECT ID_Bus FROM Bus ORDER BY ID_Bus DESC;";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            if (!db.result.next()) {
                // Tidak ada data sama sekali
                ID = "BS" + tanggalHariIni + "-0001";
            } else {
                String lastId = db.result.getString("ID_Bus");
                String lastTanggal = lastId.substring(2, 10); // Ambil bagian YYYYMMDD dari ID lama

                if (!lastTanggal.equals(tanggalHariIni)) {
                    // Jika tanggal berbeda, mulai dari 0001 lagi
                    ID = "BS" + tanggalHariIni + "-0001";
                } else {
                    String angka = lastId.split("-")[1]; // Ambil angka setelah tanda '-'
                    int prefix = Integer.parseInt(angka);
                    ID = "BS" + tanggalHariIni + "-" + String.format("%04d", prefix + 1);
                }
            }

        } catch (SQLException e) {
            System.out.println("GAGAL: " + e.getMessage());
        }
        textID.setText(ID);
        textID.setEditable(false);
    }

    private void setMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > maxLength) {
                textField.setText(oldVal); // atau pakai substring kalau mau auto potong
                // textField.setText(newVal.substring(0, maxLength));
            }
        });
    }

    public void clear(){
        refresh();
        textKapasitas.setText("");
        textID.setText("");
        textNoPolisi.setText("");
        cbJenis.setValue(null);
        mengambilID();
    }
}
