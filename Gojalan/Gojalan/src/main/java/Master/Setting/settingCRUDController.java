package Master.Setting;

import DBConnect.DBConnect;
import com.example.gojalan.DashboardKepalaController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;


public class settingCRUDController implements Initializable {
    @FXML
    private TableView<Setting> tblSetting;

    @FXML
    private TableColumn<Setting, Integer> clmID;

    @FXML
    private TableColumn<Setting, String>  clmNama;

    @FXML
    private TableColumn<Setting, Integer>  clmNo;

    @FXML
    private TableColumn<Setting, String>  clmValue;

    @FXML
    private TableColumn<Setting, String>  clmKategori1;

    @FXML
    private TableColumn<Setting, String>  clmKategori2;

    @FXML
    private TableColumn<Setting, Void>  clmAksi;

    @FXML
    private Label lblValidasi;

    @FXML
    private ComboBox<String> textNama;

    @FXML
    private TextField textValue;

    @FXML
    private DatePicker textKategori1;

    @FXML
    private DatePicker textKategori2;

    @FXML
    private TextField textCari;


    @FXML
    private Button btnTambah;

    String ID="0";
    public String usr;
    String Kategori1 = null, Kategori2 = null;


    ObservableList<Setting> oblist = FXCollections.observableArrayList();
    DBConnect db = new DBConnect();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clmNo.setCellValueFactory(new PropertyValueFactory<>("No"));
        clmID.setCellValueFactory(new PropertyValueFactory<>("id_setting"));
        clmNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        clmValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        clmKategori1.setCellValueFactory(new PropertyValueFactory<>("kategori1"));
        clmKategori2.setCellValueFactory(new PropertyValueFactory<>("kategori2"));

        clmNo.setStyle("-fx-alignment: CENTER;");
        clmKategori1.setStyle("-fx-alignment: CENTER;");
        clmKategori2.setStyle("-fx-alignment: CENTER;");
        clmNama.setStyle("-fx-alignment: CENTER;");
        clmValue.setStyle("-fx-alignment: CENTER;");

        setMaxLength(textValue, 20);

        addButtonToTable();
        tblSetting.setItems(oblist);
        tblSetting.setStyle("-fx-font-size: 19px");

        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            oblist.clear(); // bersihkan isi tabel sebelumnya
            cariSetting(newVal);
        });

        textKategori1.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Disable tanggal sebelum hari ini
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #aaaaaa;");
                }
            }
        });

        textKategori2.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Disable tanggal sebelum hari ini
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #aaaaaa;");
                }
            }
        });

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9/]*")) {
                return change;
            }
            return null;
        };

        textKategori1.getEditor().setTextFormatter(new TextFormatter<>(filter));
        textKategori2.getEditor().setTextFormatter(new TextFormatter<>(filter));

        tblSetting.refresh();

        loadNama();
        loadSetting();
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
    public void loadNama(){
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Jabatan");
        list.add("Tipe Bus");
        list.add("Harga");
        textNama.setItems(list);
    }


    @FXML
    public void loadSetting(){
        try{
            String query = "{call sp_getListSetting()}";
            CallableStatement stmt = db.conn.prepareCall(query);
            db.result = stmt.executeQuery();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while(db.result.next()) {
                String ktg1 = null;
                String ktg2 = null;

                if (db.result.getDate("Kategori1") != null) {
                    ktg1 = db.result.getDate("Kategori1").toLocalDate().format(formatter);
                }

                if (db.result.getDate("Kategori2") != null) {
                    ktg2 = db.result.getDate("Kategori2").toLocalDate().format(formatter);
                }

                oblist.add(new Setting(
                        db.result.getInt("No"),
                        db.result.getInt("ID_Setting"),
                        db.result.getString("Nama"),
                        db.result.getString("Value"),
                        ktg1,
                        ktg2
                ));
            }

            db.stat.close();
            db.result.close();
        } catch(Exception ex){
            System.out.println("Terjadi error saat load data setting : " + ex);
        }
    }

    @FXML
    public void onTableMouseClicked() {
    }

    public void setUsername(String usrnm){
        this.usr = usrnm;
    }

    @FXML
    public void clear(){
        lblValidasi.setVisible(false);
        ID = "0";
        textNama.getSelectionModel().clearSelection();
        textValue.setText("");
        textKategori1.setValue(null);
        textKategori2.setValue(null);
    }

    @FXML
    public void refresh(){
        tblSetting.getItems().clear();
        loadSetting();
    }



    @FXML
    public void onBtnSimpanClick() {
        String Nama,Value;

        int ID = Integer.parseInt(this.ID);
        int status = 1;
        Nama = (String) textNama.getSelectionModel().getSelectedItem();
        Value = textValue.getText();



        try {
            DBConnect db = new DBConnect();
            String query = "{call Sp_InsertSetting(?, ?, ?, ?, ?, ?, ?)}";

            CallableStatement cstmt = db.conn.prepareCall(query);
            cstmt.setInt(1, ID);
            cstmt.setString(2, Nama);
            cstmt.setString(3, Value);
            cstmt.setDate(4, textKategori1.getValue() != null ? java.sql.Date.valueOf(textKategori1.getValue()) : null);
            cstmt.setDate(5, textKategori2.getValue() != null ? java.sql.Date.valueOf(textKategori2.getValue()) : null);
            cstmt.setString(6, usr);
            cstmt.setInt(7, status);

            cstmt.execute();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Data berhasil ditambahkan!");
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();

            cstmt.close();
            db.conn.close();
            clear();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : " + ex.getMessage());
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
        }
        refresh();
    }

    @FXML
    public void cariSetting(String kataKunci) {

        int stts = 1;


        try {
            oblist.clear();
            String query = "{call sp_getListSetting(?,?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, kataKunci); // parameter dari textfield
            stmt.setInt(2, stts);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                oblist.add(new Setting(
                        rs.getInt("No"),
                        rs.getInt("ID_Setting"),
                        rs.getString("Nama"),
                        rs.getString("Value"),
                        rs.getString("Kategori1"),
                        rs.getString("Kategori2")
                ));
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal mencari setting: " + e.getMessage());
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<Setting, Void>, TableCell<Setting, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Setting, Void> call(final TableColumn<Setting, Void> param) {
                final TableCell<Setting, Void> cell = new TableCell<>() {

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
                            Setting setting = getTableView().getItems().get(getIndex());
                            // Ambil data dan isi ke form
                            ID = String.valueOf(setting.getId_setting());
                            textNama.getSelectionModel().select(setting.getNama());
                            textValue.setText(setting.getValue());

                            if (setting.getKategori1() != null && !setting.getKategori1().isEmpty()) {
                                textKategori1.setValue(LocalDate.parse(setting.getKategori1())); // default format yyyy-MM-dd
                            } else {
                                textKategori1.setValue(null);
                            }

                            if (setting.getKategori2() != null && !setting.getKategori2().isEmpty()) {
                                textKategori2.setValue(LocalDate.parse(setting.getKategori2())); // default format yyyy-MM-dd
                            } else {
                                textKategori2.setValue(null);
                            }

                        });

                        btnHapus.setOnAction((event) -> {
                            Setting setting = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi");
                            alert.setHeaderText("Yakin ingin menghapus?");
                            alert.setContentText("Data " + setting.getNama() + " akan dihapus.");

                            // Dapatkan stage utama dari button
                            Window window = btnHapus.getScene().getWindow();
                            alert.initOwner(window);
                            alert.initModality(Modality.WINDOW_MODAL); // tidak ambil alih fokus penuh
                            alert.initStyle(StageStyle.UTILITY); // opsional agar tidak terlalu "popup besar"

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    hapusSetting(setting.getId_setting());
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

    private void hapusSetting(int id) {
        try {
            String query = "{call Sp_delete_setting(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setInt(1, id);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal menghapus: " + e.getMessage());
        }
    }



}
