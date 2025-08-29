package Master.Supir;

import DBConnect.DBConnect;
import Master.Kursi.Kursi;
import Master.Setting.Setting;
import Master.User.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

public class supirCRUDController implements Initializable {
    @FXML
    private Button btnClear;


    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnTambah;

    @FXML
    private TableView<Supir> tblSupir;

    @FXML
    private TableColumn<Supir, Void> clmAksi;

    @FXML
    private TableColumn<Supir, String> clmAlamat;

    @FXML
    private TableColumn<Supir, Integer> clmNo;

    @FXML
    private TableColumn<Supir, String> clmEmail;

    @FXML
    private TableColumn<Supir, String> clmNama;

    @FXML
    private TableColumn<Supir, String> clmNoTelepon;

    @FXML
    private TableColumn<Supir, String> clmNIK;


    @FXML
    private Label lblValidasi;

    @FXML
    private Pane paneCari;

    @FXML
    private TextField textAlamat;

    @FXML
    private TextField textCari;

    @FXML
    private TextField textEmail;

    @FXML
    private TextField textNIK;

    @FXML
    private TextField textNama;

    @FXML
    private TextField textNoTelepon;

    @FXML
    private TextField textSIM;




    ObservableList<Supir> sprlist = FXCollections.observableArrayList();
    DBConnect db = new DBConnect();
    public String usr;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clmNo.setCellValueFactory(new PropertyValueFactory<>("No"));
        clmNIK.setCellValueFactory(new PropertyValueFactory<>("NIK"));
        clmNama.setCellValueFactory(new PropertyValueFactory<>("Nama"));
        clmEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
        clmAlamat.setCellValueFactory(new PropertyValueFactory<>("Alamat"));
        clmNoTelepon.setCellValueFactory(new PropertyValueFactory<>("NoTelepon"));

        clmNo.setStyle("-fx-alignment: CENTER;");
        clmNIK.setStyle("-fx-alignment: CENTER;");
        clmNama.setStyle("-fx-alignment: CENTER;");
        clmNoTelepon.setStyle("-fx-alignment: CENTER;");
        clmEmail.setStyle("-fx-alignment: CENTER;");
        clmAlamat.setStyle("-fx-alignment: CENTER;");

        textSIM.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));

        textNoTelepon.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));

        textNIK.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            } else {
                return null;
            }
        }));

        textNama.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[a-zA-Z ]*")) {
                return change;
            } else {
                return null;
            }
        }));

        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            sprlist.clear(); // bersihkan isi tabel sebelumnya
            cariSupir(newVal);
        });


        setMaxLength(textAlamat, 100);
        setMaxLength(textNIK, 16);
        setMaxLength(textNama, 50);
        setMaxLength(textEmail, 25);
        setMaxLength(textNoTelepon, 13);
        setMaxLength(textSIM, 20);

        addButtonToTable();
        tblSupir.setItems(sprlist);
        tblSupir.setStyle("-fx-font-size: 17px");
        loadSupir();
    }

    public void setUsername(String usrnm){
        this.usr = usrnm;
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
    public void cariSupir(String kataKunci) {

        int stts = 1;

        try {
            sprlist.clear();
            String query = "{call sp_getListSupir(?,?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, kataKunci); // parameter dari textfield
            stmt.setInt(2, stts);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                sprlist.add(new Supir(
                        rs.getInt("No"),
                        rs.getString("NIK_Supir"),
                        rs.getString("Nama"),
                        rs.getString("Email"),
                        rs.getString("Alamat"),
                        rs.getString("No_SIM"),
                        rs.getString("No_Telepon")

                ));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal mencari Supir: " + e.getMessage());
        }
    }

    @FXML
    public void loadSupir(){
        try{
            String query = "{call sp_getListSupir()}";
            CallableStatement stmt = db.conn.prepareCall(query);
            db.result = stmt.executeQuery();

            while(db.result.next()){
                sprlist.add(new Supir(
                        db.result.getInt("No"),
                        db.result.getString("NIK_Supir"),
                        db.result.getString("Nama"),
                        db.result.getString("Email"),
                        db.result.getString("Alamat"),
                        db.result.getString("No_SIM"),
                        db.result.getString("No_Telepon")

                ));
            }

            db.stat.close();
            db.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data Supir  : " + ex);
        }
    }

    @FXML
    public void onBtnSimpanClick() {
        DBConnect db = new DBConnect();
        String Nama = textNama.getText();
        String NIK = textNIK.getText();
        String Email = textEmail.getText();
        String Alamat = textAlamat.getText();
        String noSIM = textSIM.getText();
        String NoTelepon = textNoTelepon.getText();

        try {
            String query = "{call Sp_InsertSupir(?, ?, ?, ?, ?, ?, ?)}";

            CallableStatement cstmt = db.conn.prepareCall(query);
            cstmt.setString(1, NIK);
            cstmt.setString(2, Email);
            cstmt.setString(3, Nama);
            cstmt.setString(4, Alamat);
            cstmt.setString(5, noSIM);
            cstmt.setString(6, NoTelepon);
            cstmt.setString(7, usr);

            cstmt.execute();
            cstmt.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berhasil");
            alert.setHeaderText(null);
            alert.setContentText("Data berhasil ditambahkan!");
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
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

    @FXML
    public void refresh(){
        clear();
        tblSupir.getItems().clear();
        loadSupir();
    }

    @FXML
    public void clear(){
        lblValidasi.setVisible(false);
        textNIK.setText("");
        textNama.setText("");
        textEmail.setText("");
        textSIM.setText("");
        textAlamat.setText("");
        textNoTelepon.setText("");
        textNIK.setEditable(true);
    }

    private void addButtonToTable() {
        Callback<TableColumn<Supir, Void>, TableCell<Supir, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Supir, Void> call(final TableColumn<Supir, Void> param) {
                final TableCell<Supir, Void> cell = new TableCell<>() {

                    private final Button btnEdit = new Button("📝");
                    private final Button btnHapus = new Button("❌");
                    private final HBox hbox = new HBox(5); // jarak antar tombol

                    {
                        btnEdit.setStyle(
                                "-fx-font-size: 15px;" +
                                        "-fx-min-width: 36px;" +
                                        "-fx-min-height: 36px;" +
                                        "-fx-background-color: #4CAF50;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-background-radius: 6px;"
                        );
                        btnEdit.setFont(Font.font("Segoe UI Emoji", 14));
                        btnEdit.getStyleClass().addAll("button", "btn-edit");

                        btnHapus.setStyle(
                                "-fx-font-size: 15px;" +
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
                            Supir spr = getTableView().getItems().get(getIndex());
                            // Ambil data dan isi ke form
                            textNIK.setText(spr.getNIK());
                            textNIK.setEditable(false);
                            textNama.setText(spr.getNama());

                            textEmail.setText(spr.getEmail());
                            textSIM.setText(spr.getNoSIM());

                            textAlamat.setText(spr.getAlamat());
                            textNoTelepon.setText(spr.getNoTelepon());

                        });

                        btnHapus.setOnAction((event) -> {
                            Supir spr = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi");
                            alert.setHeaderText("Yakin ingin menghapus?");
                            alert.setContentText("Data " + spr.getNIK() + " akan dihapus.");

                            // Dapatkan stage utama dari button
                            Window window = btnHapus.getScene().getWindow();
                            alert.initOwner(window);
                            alert.initModality(Modality.WINDOW_MODAL); // tidak ambil alih fokus penuh
                            alert.initStyle(StageStyle.UTILITY); // opsional agar tidak terlalu "popup besar"

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    hapusSupir(spr.getNIK());
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

    private void hapusSupir(String NIK) {
        try {
            String query = "{call sp_delete_supir(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, NIK);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal menghapus: " + e.getMessage());
        }
    }

}
