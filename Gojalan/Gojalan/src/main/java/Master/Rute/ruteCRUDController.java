package Master.Rute;

import DBConnect.DBConnect;
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

public class ruteCRUDController implements Initializable {

    @FXML
    private Button btnClear;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnTambah;

    @FXML
    private TableColumn<Rute, String> clmID;

    @FXML
    private TableColumn<Rute, Void> clmJAksi;

    @FXML
    private TableColumn<Rute, Integer> clmJarak;

    @FXML
    private TableColumn<Rute, Integer> clmNo;

    @FXML
    private TableColumn<Rute, String> clmRuteAsal;

    @FXML
    private TableColumn<Rute, String> clmRuteTujuan;

    @FXML
    private Label lblValidasi;

    @FXML
    private Pane paneCari;

    @FXML
    private TableView<Rute> tblRute;

    @FXML
    private TextField textCari;

    @FXML
    private TextField textID;

    @FXML
    private TextField textJarak;

    @FXML
    private TextField textRuteAsal;

    @FXML
    private TextField textRuteTujuan;


    @FXML
    public void refresh(){
        tblRute.getItems().clear();
        loadRute();
    }

    String usr = "";

    ObservableList<Rute> uslist = FXCollections.observableArrayList();
    DBConnect db = new DBConnect();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clmNo.setCellValueFactory(new PropertyValueFactory<>("No"));
        clmID.setCellValueFactory(new PropertyValueFactory<>("ID_Rute"));
        clmRuteAsal.setCellValueFactory(new PropertyValueFactory<>("RuteAsal"));
        clmRuteTujuan.setCellValueFactory(new PropertyValueFactory<>("RuteTujuan"));
        clmJarak.setCellValueFactory(new PropertyValueFactory<>("Jarak"));
        clmJarak.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item + " KM");
                }
            }
        });

        textJarak.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                textJarak.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        textRuteAsal.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z\\s]*")) {
                textRuteAsal.setText(newVal.replaceAll("[^a-zA-Z\\s]", ""));
            }
        });


        textRuteTujuan.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[a-zA-Z\\s]*")) {
                textRuteTujuan.setText(newVal.replaceAll("[^a-zA-Z\\s]", ""));
            }
        });

        setMaxLength(textJarak,3);


        clmNo.setStyle("-fx-alignment: CENTER;");
        clmID.setStyle("-fx-alignment: CENTER;");
        clmRuteTujuan.setStyle("-fx-alignment: CENTER;");
        clmJarak.setStyle("-fx-alignment: CENTER-RIGHT;");
        clmRuteAsal.setStyle("-fx-alignment: CENTER;");
        clmJAksi.setStyle("-fx-alignment: CENTER;");


        addButtonToTable();
        tblRute.setItems(uslist);
        tblRute.setStyle("-fx-font-size: 20px");

        //menambah
        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            uslist.clear(); // bersihkan isi tabel sebelumnya
            cariRute(newVal);
        });

        tblRute.refresh();

        loadRute();
        mengambilID();
        setMaxLength(textRuteAsal, 25);
        setMaxLength(textRuteTujuan, 25);
    }

    @FXML
    public void cariRute(String kataKunci) {

        int stts = 1;


        try {
            uslist.clear();
            String query = "{call sp_getListRute(?,?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, kataKunci); // parameter dari textfield
            stmt.setInt(2, stts);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                uslist.add(new Rute(
                        rs.getInt("No"),
                        rs.getString("ID_Rute"),
                        rs.getString("Rute_Asal"),
                        rs.getString("Rute_Tujuan"),
                        rs.getInt("Jarak")));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal mencari setting: " + e.getMessage());
        }
    }



    private void addButtonToTable() {
        Callback<TableColumn<Rute, Void>, TableCell<Rute, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Rute, Void> call(final TableColumn<Rute, Void> param) {
                final TableCell<Rute, Void> cell = new TableCell<>() {

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
                            Rute rute = getTableView().getItems().get(getIndex());
                            // Ambil data dan isi ke form
                            textID.setText(rute.getID_Rute());
                            textRuteAsal.setText(rute.getRuteAsal());
                            textRuteTujuan.setText(rute.getRuteTujuan());
                            textJarak.setText(String.valueOf(rute.getJarak()));
                        });

                        btnHapus.setOnAction((event) -> {
                            Rute rute = getTableView().getItems().get(getIndex());
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi");
                            alert.setHeaderText("Yakin ingin menghapus?");
                            alert.setContentText("Data " + rute.getID_Rute() + " akan dihapus.");

                            // Dapatkan stage utama dari button
                            Window window = btnHapus.getScene().getWindow();
                            alert.initOwner(window);
                            alert.initModality(Modality.WINDOW_MODAL); // tidak ambil alih fokus penuh
                            alert.initStyle(StageStyle.UTILITY); // opsional agar tidak terlalu "popup besar"

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    hapusRute(rute.getID_Rute());
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
        clmJAksi.setCellFactory(cellFactory);
    }



    public void loadRute(){
        try{
            String query = "{call sp_getListRute()}";
            CallableStatement stmt = db.conn.prepareCall(query);
            db.result = stmt.executeQuery();
            while (db.result.next()) {
                uslist.add(new Rute(
                        db.result.getInt("No"),
                        db.result.getString("ID_Rute"),
                        db.result.getString("Rute_Asal"),
                        db.result.getString("Rute_Tujuan"),
                        db.result.getInt("Jarak")));
            }
            db.stat.close();
            db.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data User  : " + ex);
        }
    }


    private void hapusRute(String ID) {
        try {
            String query = "{call sp_delete_Rute(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, ID);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal menghapus: " + e.getMessage());
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

            String query = "SELECT ID_Rute FROM Rute ORDER BY ID_Rute DESC;";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            if (!db.result.next()) {
                // Tidak ada data sama sekali
                ID = "RT" + tanggalHariIni + "-0001";
            } else {
                String lastId = db.result.getString("ID_Rute");
                String lastTanggal = lastId.substring(2, 10); // Ambil bagian YYYYMMDD dari ID lama

                if (!lastTanggal.equals(tanggalHariIni)) {
                    // Jika tanggal berbeda, mulai dari 0001 lagi
                    ID = "RT" + tanggalHariIni + "-0001";
                } else {
                    String angka = lastId.split("-")[1]; // Ambil angka setelah tanda '-'
                    int prefix = Integer.parseInt(angka);
                    ID = "RT" + tanggalHariIni + "-" + String.format("%04d", prefix + 1);
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




    //menambah dan update
    public void setUsername(String usrnm){
        this.usr = usrnm;
    }

    @FXML
    public void onBtnSimpanClick() {
        String ID = textID.getText();
        int jarak = 0;

        if (!textJarak.getText().isEmpty()) {
            jarak = Integer.parseInt(textJarak.getText());
        }

        String Asal = textRuteAsal.getText();
        String Tujuan = textRuteTujuan.getText();

        System.out.println("nih ID lu : " + ID);
        System.out.println("ini jarak : " + jarak);
        System.out.println("asal : " + Asal);
        System.out.println("tujuan : " + Tujuan);
        System.out.println(usr);



        try {
            String query = "{call Sp_InsertRute(?, ?, ?, ?, ?)}";

            CallableStatement cstmt = db.conn.prepareCall(query);
            cstmt.setString(1, ID);
            cstmt.setString(2, Asal);
            cstmt.setString(3, Tujuan);
            cstmt.setInt(4, jarak);
            cstmt.setString(5, usr);

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
            ex.printStackTrace();
        }
    }


    public void clear(){
        refresh();
        textJarak.setText("");
        textID.setText("");
        textRuteAsal.setText("");
        textRuteTujuan.setText("");
        mengambilID();
    }
}