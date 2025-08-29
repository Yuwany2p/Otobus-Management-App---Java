package Master.Tiket;

import DBConnect.DBConnect;
import Master.Bus.Bus;
import Master.Rute.Rute;
import Master.Supir.Supir;
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
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TiketCRUDController implements Initializable {

    @FXML
    private Button btnClear;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnTambah;

    @FXML
    private TableColumn<Tiket, Double> clmHarga;

    @FXML
    private TableColumn<Tiket, Integer> clmJumlah;

    @FXML
    private TableColumn<Tiket, String> clmKeberangkatan;

    @FXML
    private TableColumn<Tiket, String> clmKedatangan;

    @FXML
    private TableColumn<Tiket, Void> clmAksi;

    @FXML
    private ComboBox<Supir> cbNamaSupir;

    @FXML
    private ComboBox<Bus> cbNoPolisi;

    @FXML
    private ComboBox<Rute> cbRuteTujuan;

    @FXML
    private ComboBox<Rute> cbRuteAsal;

    @FXML
    private TableColumn<Tiket, Integer> clmNo;

    @FXML
    private TableColumn<Tiket, String> clmNamaSupir;

    @FXML
    private TableColumn<Tiket, String> clmNomorPolisi;

    @FXML
    private DatePicker dateKeberangkatan;

    @FXML
    private Spinner<Integer> jamKeberangkatan;

    @FXML
    private Spinner<Integer> menitKeberangkatan;

    @FXML
    private DatePicker dateKedatangan;

    @FXML
    private Spinner<Integer> jamKedatangan;

    @FXML
    private Spinner<Integer> menitKedatangan;

    @FXML
    private Label lblValidasi;

    @FXML
    private Pane paneCari;

    @FXML
    private TableView<Tiket> tblTiket;

    @FXML
    private TextField textCari;

    @FXML
    private TextField textHarga;

    @FXML
    private TextField textIDTiket;

    @FXML
    private TextField textJumlahTiket;

    public String usr;

    ObservableList<Tiket> tkList = FXCollections.observableArrayList();
    DBConnect db = new DBConnect();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clmNo.setCellValueFactory(new PropertyValueFactory<>("No"));
        clmNomorPolisi.setCellValueFactory(new PropertyValueFactory<>("Nomor_Polisi"));
        clmNamaSupir.setCellValueFactory(new PropertyValueFactory<>("Nama"));
        clmJumlah.setCellValueFactory(new PropertyValueFactory<>("Jumlah_Tiket"));
        clmHarga.setCellValueFactory(new PropertyValueFactory<>("Harga"));

        clmHarga.setCellFactory(column -> new TableCell<Tiket, Double>() {
            @Override
            protected void updateItem(Double harga, boolean empty) {
                super.updateItem(harga, empty);
                if (empty || harga == null) {
                    setText(null);
                } else {
                    setText(formatRupiah(harga.longValue()));
                }
            }
        });
        dateKeberangkatan.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Disable tanggal sebelum hari ini
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // opsional: warna merah muda
                }
            }
        });

        dateKedatangan.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Disable tanggal sebelum hari ini
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // opsional: warna merah muda
                }
            }
        });


        dateKeberangkatan.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dateKedatangan.setValue(newDate); // Atur default value ke tanggal yang sama

                dateKedatangan.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        // Nonaktifkan tanggal sebelum tanggal keberangkatan
                        setDisable(empty || item.isBefore(newDate));
                    }
                });
            }
        });

        clmKeberangkatan.setCellValueFactory(new PropertyValueFactory<>("Keberangkatan"));
        clmKedatangan.setCellValueFactory(new PropertyValueFactory<>("Kedatangan"));

        clmNamaSupir.setStyle("-fx-alignment: CENTER;");
        clmJumlah.setStyle("-fx-alignment: CENTER-RIGHT;");
        clmHarga.setStyle("-fx-alignment: CENTER-RIGHT;");
        clmKeberangkatan.setStyle("-fx-alignment: CENTER;");
        clmKedatangan.setStyle("-fx-alignment: CENTER;");
        clmNo.setStyle("-fx-alignment: CENTER;");
        clmNomorPolisi.setStyle("-fx-alignment: CENTER;");

        cbNoPolisi.setStyle("-fx-font-size: 16px; -fx-border-radius: 10; -fx-background-radius: 10");
        cbNamaSupir.setStyle("-fx-font-size: 16px; -fx-border-radius: 10; -fx-background-radius: 10");
        cbRuteTujuan.setStyle("-fx-font-size: 16px; -fx-border-radius: 10; -fx-background-radius: 10");
        cbRuteAsal.setStyle("-fx-font-size: 16px; -fx-border-radius: 10; -fx-background-radius: 10");

        dateKeberangkatan.setStyle("-fx-font-size: 16px; -fx-border-radius: 10; -fx-background-radius: 10");
        dateKedatangan.setStyle("-fx-font-size: 16px; -fx-border-radius: 10; -fx-background-radius: 10");

        jamKeberangkatan.setStyle("-fx-font-size: 14px; ");
        menitKeberangkatan.setStyle("-fx-font-size: 14px; -fx-background-radius: 10");
        jamKedatangan.setStyle("-fx-font-size: 14px;");
        menitKedatangan.setStyle("-fx-font-size: 14px; -fx-background-radius: 10");

        addButtonToTable();
        tblTiket.setItems(tkList);
        tblTiket.setStyle("-fx-font-size: 19px");

        SpinnerValueFactory<Integer> jamFactoryKeberangkatan =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0); // default jam 0
        SpinnerValueFactory<Integer> menitFactoryKeberangkatan =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0); // default menit 0

        // Spinner jam & menit untuk kedatangan (default jam 0 menit 0)
        SpinnerValueFactory<Integer> jamFactoryKedatangan =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0); // default jam 0
        SpinnerValueFactory<Integer> menitFactoryKedatangan =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);

        jamKeberangkatan.setValueFactory(jamFactoryKeberangkatan);
        menitKeberangkatan.setValueFactory(menitFactoryKeberangkatan);
        jamKedatangan.setValueFactory(jamFactoryKedatangan);
        menitKedatangan.setValueFactory(menitFactoryKedatangan);

        // Default tanggal hari ini
        dateKeberangkatan.setValue(LocalDate.now());
        dateKedatangan.setValue(LocalDate.now());

        jamKeberangkatan.setEditable(true);
        menitKeberangkatan.setEditable(true);
        jamKedatangan.setEditable(true);
        menitKedatangan.setEditable(true);

        addSpinnerValidation(jamKeberangkatan, 0, 23);
        addSpinnerValidation(menitKeberangkatan, 0, 59);
        addSpinnerValidation(jamKedatangan, 0, 23);
        addSpinnerValidation(menitKedatangan, 0, 59);

        dateKeberangkatan.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dateKedatangan.setValue(newDate); // Atur default value ke tanggal yang sama

                dateKedatangan.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        // Nonaktifkan tanggal sebelum tanggal keberangkatan
                        setDisable(empty || item.isBefore(newDate));
                    }
                });
            }
        });

        cbNoPolisi.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Bus item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "Pilih Nomor Polisi" : item.getNoPolisi());
            }
        });

        cbNamaSupir.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Supir item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "Pilih Nama Supir" : item.getNama());
            }
        });

        cbRuteTujuan.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Rute item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "Pilih Rute Tujuan" : item.getRuteTujuan());
            }
        });

        textJumlahTiket.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                textJumlahTiket.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        // textHarga hanya angka, tapi tetap tampilkan sebagai Rp
        textHarga.textProperty().addListener((obs, oldVal, newVal) -> {
            String digitOnly = newVal.replaceAll("[^\\d]", ""); // hanya angka

            if (!digitOnly.isEmpty()) {
                try {
                    long value = Long.parseLong(digitOnly);
                    textHarga.setText(formatRupiah(value));
                } catch (NumberFormatException e) {
                    textHarga.setText("Rp. 0");
                }
            } else {
                textHarga.setText(""); // kosongkan jika tidak ada angka
            }
        });

        // Maksimal 30 karakter, hanya angka untuk textJumlahTiket
        textJumlahTiket.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                newVal = newVal.replaceAll("[^\\d]", "");
            }
            if (newVal.length() > 30) {
                newVal = newVal.substring(0, 30);
            }
            textJumlahTiket.setText(newVal);
        });

        // Maksimal 30 digit angka, format ke Rupiah otomatis
        textHarga.textProperty().addListener((obs, oldVal, newVal) -> {
            String digitOnly = newVal.replaceAll("[^\\d]", "");

            if (digitOnly.length() > 30) {
                digitOnly = digitOnly.substring(0, 30);
            }

            if (!digitOnly.isEmpty()) {
                try {
                    long value = Long.parseLong(digitOnly);
                    textHarga.setText(formatRupiah(value));
                } catch (NumberFormatException e) {
                    textHarga.setText("Rp. 0");
                }
            } else {
                textHarga.setText("");
            }
        });

        textCari.textProperty().addListener((obs, oldVal, newVal) -> {
            tkList.clear(); // bersihkan isi tabel sebelumnya
            cariTiket(newVal);
        });


        setMaxLength(textHarga,40);


        loadTiket();
        loadComboBoxNoPolisi();
        loadComboBoxNama();
        loadComboBoxRuteAsal();
        addButtonToTable();
        mengambilID();
    }

    private String formatRupiah(long nilai) {
        return String.format("Rp. %,d", nilai).replace(',', '.');
    }

    private void setMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > maxLength) {
                textField.setText(oldVal); // atau pakai substring kalau mau auto potong
                // textField.setText(newVal.substring(0, maxLength));
            }
        });
    }

    private void addSpinnerValidation(Spinner<Integer> spinner, int min, int max) {
        spinner.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                spinner.getEditor().setText(newVal.replaceAll("[^\\d]", ""));
                return;
            }

            if (!newVal.isEmpty()) {
                try {
                    int value = Integer.parseInt(newVal);
                    if (value < min) {
                        spinner.getEditor().setText(String.valueOf(min));
                    } else if (value > max) {
                        spinner.getEditor().setText(String.valueOf(max));
                    } else {
                        spinner.getValueFactory().setValue(value);
                    }
                } catch (NumberFormatException e) {
                    spinner.getEditor().setText(String.valueOf(min));
                }
            }
        });
    }

    @FXML
    public void cariTiket(String kataKunci) {


        try {
            tkList.clear();
            String query = "{call sp_getListTiket(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, kataKunci); // parameter dari textfield

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tkList.add(new Tiket(
                        rs.getInt("No"),
                        rs.getString("ID_Tiket"),
                        rs.getString("NIK_Supir"),
                        rs.getString("Nama_Supir"),
                        rs.getString("ID_Bus"),
                        rs.getString("Nomor_Polisi"),
                        rs.getInt("Kapasitas"),
                        rs.getString("ID_Rute"),
                        rs.getString("Rute_Asal"),
                        rs.getString("Rute_Tujuan"),
                        rs.getInt("Jarak"),
                        rs.getInt("ID_Setting"),
                        rs.getString("Kepulangan"),
                        rs.getString("Keberangkatan"),
                        rs.getDouble("Harga"),
                        rs.getInt("Jumlah_Tiket")));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Gagal mencari Tiket: " + e.getMessage());
        }
    }

    @FXML
    public void refresh(){
        tblTiket.getItems().clear();
        loadTiket();
    }

    @FXML
    void clear() {
        refresh();
        mengambilID();

        // Reset ComboBox & PromptText
        cbNoPolisi.getSelectionModel().clearSelection();
        cbNoPolisi.setPromptText("Pilih Nomor Polisi");

        cbNamaSupir.getSelectionModel().clearSelection();
        cbNamaSupir.setPromptText("Pilih Nama Supir");

        cbRuteAsal.getSelectionModel().clearSelection();
        cbRuteAsal.setPromptText("Pilih Rute Asal");

        cbRuteTujuan.getSelectionModel().clearSelection();
        cbRuteTujuan.setPromptText("Pilih Rute Tujuan");

        // Reset tanggal ke hari ini
        dateKeberangkatan.setValue(LocalDate.now());
        dateKedatangan.setValue(LocalDate.now());

        // Reset jam dan menit ke 0
        jamKeberangkatan.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        menitKeberangkatan.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        jamKedatangan.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        menitKedatangan.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        // Kosongkan input harga & jumlah tiket
        textHarga.clear();
        textJumlahTiket.clear();

        // Ambil ID Tiket baru & set readonly
        mengambilID();
    }


    public String getIDRuteByAsalDanTujuan() {
        String idRute = null;

        // Pastikan combo box tidak kosong/null
        if (cbRuteAsal.getValue() == null || cbRuteTujuan.getValue() == null) {
            System.out.println("Rute asal atau tujuan belum dipilih.");
            return null;
        }

        String asal = cbRuteAsal.getValue().getRuteAsal();
        String tujuan = cbRuteTujuan.getValue().getRuteTujuan();

        try {
            String sql = "SELECT ID_Rute FROM Rute WHERE Rute_Asal = ? AND Rute_Tujuan = ? AND Status = 1";
            PreparedStatement stmt = db.conn.prepareStatement(sql);
            stmt.setString(1, asal);
            stmt.setString(2, tujuan);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idRute = rs.getString("ID_Rute");
            } else {
                System.out.println("ID_Rute tidak ditemukan untuk asal " + asal + " dan tujuan " + tujuan);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idRute;
    }


    @FXML
    public void onBtnSimpanClick() {
        try {
            String idTiket = textIDTiket.getText();
            String nikSupir = "";
            Supir selectedSetting = cbNamaSupir.getSelectionModel().getSelectedItem();
            if (selectedSetting != null) {
                nikSupir = selectedSetting.getNIK();
            }
            String idBus = "";
            Bus selectedBus = cbNoPolisi.getSelectionModel().getSelectedItem();
            if (selectedBus != null) {
                idBus = selectedBus.getID_Bus();
            }
            String idRute = getIDRuteByAsalDanTujuan();

            int idSetting = 5;
            double harga = Double.parseDouble(textHarga.getText().replaceAll("[^\\d]", ""));
            int jumlahTiket = Integer.parseInt(textJumlahTiket.getText());

            // Gabungkan Tanggal + Jam + Menit → Keberangkatan
            LocalDateTime keberangkatan = LocalDateTime.of(
                    dateKeberangkatan.getValue(),
                    LocalTime.of(jamKeberangkatan.getValue(), menitKeberangkatan.getValue())
            );

            // Gabungkan Tanggal + Jam + Menit → Kepulangan
            LocalDateTime kepulangan = LocalDateTime.of(
                    dateKedatangan.getValue(),
                    LocalTime.of(jamKedatangan.getValue(), menitKedatangan.getValue())
            );

            // Panggil SP
            String query = "{call Sp_InsertTiket(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement stmt = db.conn.prepareCall(query);

            stmt.setString(1, idTiket);
            stmt.setString(2, nikSupir);
            stmt.setString(3, idBus);
            stmt.setString(4, idRute);
            stmt.setInt(5, idSetting);
            stmt.setTimestamp(6, Timestamp.valueOf(keberangkatan));
            stmt.setTimestamp(7, Timestamp.valueOf(kepulangan));
            stmt.setDouble(8, harga);
            stmt.setInt(9, jumlahTiket);
            stmt.setString(10, usr); // CreatedBy

            stmt.execute();
            stmt.close();

            // Notifikasi berhasil
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
            ex.printStackTrace();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : " + ex.getMessage());
            alert.initOwner(lblValidasi.getScene().getWindow());
            alert.showAndWait();
            ex.printStackTrace();
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

            String query = "SELECT ID_Tiket FROM Tiket ORDER BY ID_Tiket DESC;";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            if (!db.result.next()) {
                // Tidak ada data sama sekali
                ID = "TK" + tanggalHariIni + "-0001";
            } else {
                String lastId = db.result.getString("ID_Tiket");
                String lastTanggal = lastId.substring(2, 10); // Ambil bagian YYYYMMDD dari ID lama

                if (!lastTanggal.equals(tanggalHariIni)) {
                    // Jika tanggal berbeda, mulai dari 0001 lagi
                    ID = "TK" + tanggalHariIni + "-0001";
                } else {
                    String angka = lastId.split("-")[1]; // Ambil angka setelah tanda '-'
                    int prefix = Integer.parseInt(angka);
                    ID = "TK" + tanggalHariIni + "-" + String.format("%04d", prefix + 1);
                }
            }

        } catch (SQLException e) {
            System.out.println("GAGAL: " + e.getMessage());
        }
        textIDTiket.setText(ID);
        textIDTiket.setEditable(false);
    }


    @FXML
    public void loadTiket() {
        try{
            String query = "{call sp_getListTiket()}";
            CallableStatement stmt = db.conn.prepareCall(query);
            db.result = stmt.executeQuery();
            while (db.result.next()) {
                tkList.add(new Tiket(
                        db.result.getInt("No"),
                        db.result.getString("ID_Tiket"),
                        db.result.getString("NIK_Supir"),
                        db.result.getString("Nama_Supir"),
                        db.result.getString("ID_Bus"),
                        db.result.getString("Nomor_Polisi"),
                        db.result.getInt("Kapasitas"),
                        db.result.getString("ID_Rute"),
                        db.result.getString("Rute_Asal"),
                        db.result.getString("Rute_Tujuan"),
                        db.result.getInt("Jarak"),
                        db.result.getInt("ID_Setting"),
                        db.result.getString("Kepulangan"),
                        db.result.getString("Keberangkatan"),
                        db.result.getDouble("Harga"),
                        db.result.getInt("Jumlah_Tiket")));
            }
            db.stat.close();
            db.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data Tiket  : " + ex);
        }
    }

    public void loadComboBoxNoPolisi() {
        try {
            String query = "SELECT ID_Bus,Nomor_Polisi FROM Bus WHERE Status = 1";
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


    public void loadComboBoxNama() {
        try {
            String query = "SELECT NIK_Supir,Nama FROM Supir WHERE Status = 1";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            ObservableList<Supir> list = FXCollections.observableArrayList();

            while (db.result.next()) {
                list.add(new Supir(db.result.getString("NIK_Supir"),db.result.getString("Nama")));
            }

            cbNamaSupir.setItems(list);

            db.stat.close();
            db.result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadComboBoxRuteAsal() {
        try {
            String query = "SELECT DISTINCT Rute_Asal FROM Rute WHERE Status = '1'";
            Statement stmt = db.conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            ObservableList<Rute> asalList = FXCollections.observableArrayList();

            while (rs.next()) {
                String asal = rs.getString("Rute_Asal");
                asalList.add(new Rute(asal));
            }

            cbRuteAsal.setItems(asalList); // <- setelah ini, taruh solusi 2

            // Solusi 2: menampilkan RuteAsal secara eksplisit
            cbRuteAsal.setCellFactory(param -> new ListCell<Rute>() {
                @Override
                protected void updateItem(Rute item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null) ? null : item.getRuteAsal());
                }
            });

            cbRuteAsal.setButtonCell(new ListCell<Rute>() {
                @Override
                protected void updateItem(Rute item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null) ? "Pilih Rute Asal" : item.getRuteAsal());
                }
            });

            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Listener tetap bisa di bawah
        cbRuteAsal.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadComboBoxRuteTujuan(newVal.getRuteAsal());
            }
        });
    }


    public void loadComboBoxRuteTujuan(String asalDipilih) {
        try {
            String query = "SELECT ID_Rute, Rute_Tujuan, Jarak FROM Rute WHERE Status = '1' AND Rute_Asal = ?";
            PreparedStatement stmt = db.conn.prepareStatement(query);
            stmt.setString(1, asalDipilih);

            ResultSet rs = stmt.executeQuery();
            ObservableList<Rute> tujuanList = FXCollections.observableArrayList();

            while (rs.next()) {
                tujuanList.add(new Rute(
                        0,
                        rs.getString("ID_Rute"),
                        asalDipilih,
                        rs.getString("Rute_Tujuan"),
                        rs.getInt("Jarak")
                ));
            }

            cbRuteTujuan.setItems(tujuanList);
            cbRuteTujuan.getSelectionModel().clearSelection();

            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<Tiket, Void>, TableCell<Tiket, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Tiket, Void> call(final TableColumn<Tiket, Void> param) {
                final TableCell<Tiket, Void> cell = new TableCell<>() {

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
                            Tiket tiket = getTableView().getItems().get(getIndex());

                            // ID Tiket, Harga, Jumlah
                            textIDTiket.setText(tiket.getID_Tiket());
                            textHarga.setText(formatRupiah((long) tiket.getHarga()));
                            textJumlahTiket.setText(String.valueOf(tiket.getJumlah_Tiket()));

                            // Tanggal dan waktu
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0");
                            dateKeberangkatan.setValue(LocalDateTime.parse(tiket.getKeberangkatan(), formatter).toLocalDate());
                            dateKedatangan.setValue(LocalDateTime.parse(tiket.getKedatangan(), formatter).toLocalDate());

                            // Load dan set Rute Asal
                            if (cbRuteAsal.getItems().isEmpty()) {
                                loadComboBoxRuteAsal();
                            }

                            for (Rute r : cbRuteAsal.getItems()) {
                                if (r.getRuteAsal().equals(tiket.getRute_Asal())) {
                                    cbRuteAsal.getSelectionModel().select(r);
                                    loadComboBoxRuteTujuan(r.getRuteAsal()); // <== penting, load tujuan
                                    break;
                                }
                            }

                            // Set Rute Tujuan
                            for (Rute r : cbRuteTujuan.getItems()) {
                                if (r.getRuteTujuan().equals(tiket.getRute_Tujuan())) {
                                    cbRuteTujuan.getSelectionModel().select(r);
                                    break;
                                }
                            }

                            // Set ComboBox No Polisi
                            for (Bus b : cbNoPolisi.getItems()) {
                                if (b.getID_Bus().equals(tiket.getID_Bus())) {
                                    cbNoPolisi.getSelectionModel().select(b);
                                    break;
                                }
                            }

                            // Set ComboBox Supir
                            for (Supir s : cbNamaSupir.getItems()) {
                                if (s.getNIK().equals(tiket.getNIK_Supir())) {
                                    cbNamaSupir.getSelectionModel().select(s);
                                    break;
                                }
                            }

                            // Jam & menit keberangkatan dan kepulangan
                            LocalTime waktuBerangkat = LocalDateTime.parse(tiket.getKeberangkatan(), formatter).toLocalTime();
                            LocalTime waktuKembali = LocalDateTime.parse(tiket.getKedatangan(), formatter).toLocalTime();

                            jamKeberangkatan.getValueFactory().setValue(waktuBerangkat.getHour());
                            menitKeberangkatan.getValueFactory().setValue(waktuBerangkat.getMinute());

                            jamKedatangan.getValueFactory().setValue(waktuKembali.getHour());
                            menitKedatangan.getValueFactory().setValue(waktuKembali.getMinute());
                        });



                        btnHapus.setOnAction((event) -> {
                            Tiket tiket = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi");
                            alert.setHeaderText("Yakin ingin menghapus?");
                            alert.setContentText("Data " + tiket.getID_Tiket() + " akan dihapus.");

                            // Dapatkan stage utama dari button
                            Window window = btnHapus.getScene().getWindow();
                            alert.initOwner(window);
                            alert.initModality(Modality.WINDOW_MODAL); // tidak ambil alih fokus penuh
                            alert.initStyle(StageStyle.UTILITY); // opsional agar tidak terlalu "popup besar"

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    hapusTiket(tiket.getID_Tiket());
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

    private void hapusTiket(String NIK) {
        try {
            String query = "{call Sp_delete_Tiket(?)}";
            CallableStatement stmt = db.conn.prepareCall(query);
            stmt.setString(1, NIK);

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Gagal menghapus: " + e.getMessage());
        }
    }

    public void setUsername(String usrnm){
        this.usr = usrnm;
    }

}

