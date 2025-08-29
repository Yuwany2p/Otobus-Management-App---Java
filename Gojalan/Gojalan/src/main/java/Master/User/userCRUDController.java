package Master.User;

import DBConnect.DBConnect;
import Master.Setting.Setting;
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

public class userCRUDController implements Initializable {

    @FXML
    private TableView<User> tblUser;

    @FXML
    private TableColumn<User, String> clmNIK;

    @FXML
    private TableColumn<User, Integer> clmNo;

    @FXML
    private TableColumn<User, String>  clmNama;

    @FXML
    private TableColumn<User, String>  clmJabatan;

    @FXML
    private TableColumn<User, String>  clmUsername;

    @FXML
    private TableColumn<User, String>  clmPassword;

    @FXML
    private TableColumn<User, String>  clmEmail;

    @FXML
    private TableColumn<User, Void>  clmAksi;

    @FXML
    private Label lblValidasi;

    @FXML
    private TextField textNIK;

    @FXML
    private TextField textNama;

    @FXML
    private ComboBox<Setting> cbJabatan;

    @FXML
    private TextField textCari;

    @FXML
    private TextField textUsername;

    @FXML
    private TextField textPassword;

    @FXML
    private TextField textEmail;

    @FXML
    private Button btnTambah;

    public String usr;

    ObservableList<User> uslist = FXCollections.observableArrayList();
    DBConnect db = new DBConnect();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clmNo.setCellValueFactory(new PropertyValueFactory<>("No"));
        clmNIK.setCellValueFactory(new PropertyValueFactory<>("NIK"));
        clmNama.setCellValueFactory(new PropertyValueFactory<>("Nama"));
        clmJabatan.setCellValueFactory(new PropertyValueFactory<>("Jabatan"));
        clmUsername.setCellValueFactory(new PropertyValueFactory<>("Username"));
        clmPassword.setCellValueFactory(new PropertyValueFactory<>("Password"));
        clmEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));

        clmNo.setStyle("-fx-alignment: CENTER;");
        clmNIK.setStyle("-fx-alignment: CENTER;");
        clmNama.setStyle("-fx-alignment: CENTER;");
        clmJabatan.setStyle("-fx-alignment: CENTER;");
        clmUsername.setStyle("-fx-alignment: CENTER;");
        clmPassword.setStyle("-fx-alignment: CENTER;");
        clmEmail.setStyle("-fx-alignment: CENTER;");

        setMaxLength(textNIK,16);
        setMaxLength(textNama,50);
        setMaxLength(textUsername,50);
        setMaxLength(textPassword,50);
        setMaxLength(textEmail,25);

        addButtonToTable();
        tblUser.setItems(uslist);
        tblUser.setStyle("-fx-font-size: 16px");

        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            uslist.clear(); // bersihkan isi tabel sebelumnya
            cariUser(newVal);
        });

        textNIK.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textNIK.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });


        tblUser.refresh();

        loadUser();
        loadComboBox();
    }

    @FXML
    public void refresh(){
        tblUser.getItems().clear();
        loadUser();
    }

    public void setUsername(String usrnm){
        this.usr = usrnm;
    }

    @FXML
    public void onBtnSimpanClick() {
        DBConnect db = new DBConnect();
        String Nama = textNama.getText();
        String NIK = textNIK.getText();
        String Jabatan = "";

        if (cbJabatan.getSelectionModel().getSelectedItem() != null) {
            Jabatan = cbJabatan.getSelectionModel().getSelectedItem().toString();
        }

        String Username = textUsername.getText();
        String Password = textPassword.getText();
        String Email = textEmail.getText();

        int ID_Setting = 0;
        Setting jabatanDipilih = cbJabatan.getSelectionModel().getSelectedItem();

        if (jabatanDipilih != null) {
            ID_Setting = jabatanDipilih.getId_setting();
        }


        try {
            String query = "{call Sp_InsertUser(?, ?, ?, ?, ?, ?, ?)}";

            CallableStatement cstmt = db.conn.prepareCall(query);
            cstmt.setInt(1, ID_Setting);
            cstmt.setString(2, NIK);
            cstmt.setString(3, Nama);
            cstmt.setString(4, Username);
            cstmt.setString(5, Password);
            cstmt.setString(6, Email);
            cstmt.setString(7, usr);

            cstmt.execute();
            cstmt.close();


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Data berhasil ditambahkan!");
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
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
    public void loadUser(){
        try{
            String query = "{call sp_getListUserGojalan()}";
            CallableStatement stmt = db.conn.prepareCall(query);
            db.result = stmt.executeQuery();
            while(db.result.next()){
                uslist.add(new User(
                        db.result.getInt("No"),
                        db.result.getString("NIK_User"),
                        db.result.getString("Nama"),
                        db.result.getInt("ID_Setting"),
                        db.result.getString("Jabatan"),
                        db.result.getString("Username"),
                        db.result.getString("Password"),
                        db.result.getString("Email")));

            }

            db.stat.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data User  : " + ex);
        }
    }


    public void loadComboBox() {
        try {
            String query = "SELECT Value,ID_Setting  FROM Setting WHERE Nama ='Jabatan'";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            ObservableList<Setting> list = FXCollections.observableArrayList();

            while (db.result.next()) {
                list.add(new Setting(db.result.getInt("ID_Setting"), db.result.getString("Value")));
            }

            cbJabatan.setItems(list);

            db.stat.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void clear(){
        lblValidasi.setVisible(false);
        textNIK.setText("");
        textNama.setText("");
        textUsername.setText("");
        textPassword.setText("");
        textEmail.setText("");
        textPassword.setEditable(true);
    }

    @FXML
    public void cariUser(String kataKunci) {

        int stts = 1;

        try {
            uslist.clear();
            String query = "{call sp_getListUserGojalan(?,?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, kataKunci); // parameter dari textfield
            stmt.setInt(2, stts);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                uslist.add(new User(
                        rs.getInt("No"),
                        rs.getString("NIK_User"),
                        rs.getString("Nama"),
                        rs.getInt("ID_Setting"),
                        rs.getString("Jabatan"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Email")));

            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal mencari User: " + e.getMessage());
        }
    }


    private void addButtonToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                final TableCell<User, Void> cell = new TableCell<>() {

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
                        btnEdit.setFont(Font.font("Segoe UI Emoji", 17));
                        btnEdit.getStyleClass().addAll("button", "btn-edit");

                        btnHapus.setStyle(
                                "-fx-font-size: 17px;" +
                                        "-fx-min-width: 36px;" +
                                        "-fx-min-height: 36px;" +
                                        "-fx-background-color: #F44336;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-background-radius: 6px;"
                        );
                        btnHapus.setFont(Font.font("Segoe UI Emoji", 17));
                        btnHapus.getStyleClass().addAll("button", "btn-hapus");
                        hbox.getChildren().addAll(btnEdit, btnHapus);

                        btnEdit.setOnAction((event) -> {
                            User user = getTableView().getItems().get(getIndex());
                            // Ambil data dan isi ke form
                            textNIK.setText(user.getNIK());
                            textNama.setText(user.getNama());
                            textUsername.setText(user.getUsername());
                            textPassword.setText(user.getPassword());
                            textEmail.setText(user.getEmail());
                            textPassword.setEditable(false);

                            ObservableList<Setting> daftar = cbJabatan.getItems();
                            for (int i = 0; i < daftar.size(); i++) {
                                if (daftar.get(i).getId_setting() == user.getID_Setting()) {
                                    cbJabatan.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        });

                        btnHapus.setOnAction((event) -> {
                            User user = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi");
                            alert.setHeaderText("Yakin ingin menghapus?");
                            alert.setContentText("Data " + user.getUsername() + " akan dihapus.");

                            // Dapatkan stage utama dari button
                            Window window = btnHapus.getScene().getWindow();
                            alert.initOwner(window);
                            alert.initModality(Modality.WINDOW_MODAL); // tidak ambil alih fokus penuh
                            alert.initStyle(StageStyle.UTILITY); // opsional agar tidak terlalu "popup besar"

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    hapusUser(user.getNIK());
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

    private void hapusUser(String NIK) {
        try {
            String query = "{call Sp_delete_user(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, NIK);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal menghapus: " + e.getMessage());
        }
    }

    private void setMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > maxLength) {
                textField.setText(oldVal); // atau pakai substring kalau mau auto potong
                // textField.setText(newVal.substring(0, maxLength));
            }
        });
    }
}
