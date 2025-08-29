package Transaksi.PembelianTiket;

import DBConnect.DBConnect;
import Master.Bus.Bus;
import Master.Supir.Supir;
import Master.Tiket.Tiket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class PembelianTiketController implements Initializable {

    private static final Logger log = LogManager.getLogger(PembelianTiketController.class);
    @FXML
    private ComboBox<String> cbDari;

    @FXML
    private ComboBox<String> cbKe;

    @FXML
    private DatePicker pickDatang;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label lblCari;

    @FXML
    private Label lblTotalRow;

    @FXML
    private Label lblBatal;

    @FXML
    private DatePicker pickPergi;

    @FXML
    private VBox vbTiket;

    @FXML
    private Label lblTotal;

    @FXML
    private TextArea textAlamat;

    @FXML
    private TextField textNama;

    @FXML
    private TextField textNoTelepon;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vbSelectedTiket;

    @FXML
    private Button btnBatal;

    @FXML
    private Button btnSimpan;

    private Tiket tiket;
    DBConnect db = new DBConnect();
    String ID_Pembelian, NIK, ID_Tiket, Tgl_Pembelian, No_Telepon, Nama, Alamat,CreatedBy;
    double totalHarga = 0;
    int statusPembelian = 1;
    int statusKursi = 1;
    public String usr;
    double persenKembali = 0;
    double totalPengembalian = 0;
    String TglPengembalian = null;

    private List<String> listNomorKursi = new ArrayList<>();

    private List<Button> listBtnTambah = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbDari.setStyle("-fx-font-size: 20px;");        // ComboBox
        cbKe.setStyle("-fx-font-size: 20px;");        // ComboBox
        pickPergi.setStyle("-fx-font-size: 20px;");     // DatePicker
        pickDatang.setStyle("-fx-font-size: 20px;");    // DatePicker
        pickPergi.setEditable(false);
        pickDatang.setEditable(false);


        pickPergi.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                pickDatang.setValue(newDate); // Atur default value ke tanggal yang sama

                pickDatang.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        // Nonaktifkan tanggal sebelum tanggal keberangkatan
                        setDisable(empty || item.isBefore(newDate));
                    }
                });
            }
        });

        textNoTelepon.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                newVal = newVal.replaceAll("[^\\d]", "");
            }
            if (newVal.length() > 30) {
                newVal = newVal.substring(0, 30);
            }
            textNoTelepon.setText(newVal);
        });


        setMaxLength(textNoTelepon, 13);
        loadComboBoxRute();
    }

    public void setListNomorKursi(List<String> nomorKursiBaru) {
        listNomorKursi.clear();                    // Hapus isi lama
        listNomorKursi.addAll(nomorKursiBaru);     // Tambah data baru
    }


    public void setTiket(Tiket tiket){
        this.tiket = tiket;
    }

    public void setUsername(String usrnm){
        this.usr = usrnm;
    }

    public Label getLblTotal() {
        return lblTotal;
    }

    public void setLblTotal(Label lblTotal) {
        this.lblTotal = lblTotal;
    }

    public void updateTotalHarga(int hargaTiket, int jumlahTiket) {
        int total = hargaTiket * jumlahTiket;
        totalHarga = total;
        lblTotal.setText(formatRupiah(total));
    }

    public String formatRupiah(int amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String result = formatter.format(amount);
        return result.replace("Rp", "Rp ");  // Tambah spasi
    }

    public AnchorPane getRootPane() {
        return rootPane;
    }

    public VBox getVbSelectedTiket() {
        return vbSelectedTiket;
    }

    public void addBtnTambah(Button btn) {
        listBtnTambah.add(btn);
    }

    public void disableAllBtnTambah() {
        for (Button btn : listBtnTambah) {
            btn.setDisable(true);
        }
    }

    public void inableAllBtnTambah() {
        for (Button btn : listBtnTambah) {
            btn.setDisable(false);
        }
    }

    @FXML
    public void onCariTiket() {
        loadTiket();
    }

    @FXML
    public void onBatal() {
        cbDari.getSelectionModel().clearSelection();
        cbKe.getSelectionModel().clearSelection();

        vbTiket.getChildren().clear();

        pickPergi.setValue(null);
        pickDatang.setValue(null);
    }


    @FXML
    public void onEntered(){
        lblCari.setStyle("-fx-text-fill: #7d7d7d;");
    }

    @FXML
    public void onExited(){
        lblCari.setStyle("");
    }

    @FXML
    public void onBatalEntered(){
        lblBatal.setStyle("-fx-text-fill: #7d7d7d;");
    }

    @FXML
    public void onBatalExited(){
        lblBatal.setStyle("");
    }


    public void loadComboBoxRute() {
        List<Tiket> tiketList = getAllTiket();

        // Gunakan Set untuk menghindari duplikat
        Set<String> asalSet = new HashSet<>();
        Set<String> tujuanSet = new HashSet<>();

        for (Tiket tiket : tiketList) {
            asalSet.add(tiket.getRute_Asal());
            tujuanSet.add(tiket.getRute_Tujuan());
        }

        // Ubah ke ObservableList dan isi ke ComboBox
        cbDari.setItems(FXCollections.observableArrayList(asalSet));
        cbKe.setItems(FXCollections.observableArrayList(tujuanSet));
    }

    public void loadTiket() {
        vbTiket.getChildren().clear(); // kosongkan dulu konten sebelumnya

        List<Tiket> daftarTiket = getAllTiket(); // method buatanmu untuk ambil data dari DB

        if(daftarTiket.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Bus Tidak Ditemukan");
            alert.initOwner(lblTotal.getScene().getWindow());
            alert.showAndWait();
            return;
        }

        try {
            for (Tiket tiket : daftarTiket) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Transaksi/Card.fxml"));
                Node cardNode = loader.load();

                // ambil controller-nya
                tiketController controller = loader.getController();
                controller.setParent(this);
                this.addBtnTambah(controller.getBtnTambah());
                controller.setData(tiket); // kirim data Tiket ke tampilan

                controller.setRootPane(rootPane);      // rootPane untuk modal/popup

                // tambahkan ke VBox
                vbTiket.getChildren().add(cardNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal memuat tiket: " + e.getMessage());
        }
        lblTotalRow.setText(daftarTiket.size() + " Tiket Ditemukan");
    }

    public List<Tiket> getAllTiket() {
        List<Tiket> tkList = new ArrayList<>();
        String asal = cbDari.getValue();
        String tujuan = cbKe.getValue();
        LocalDate tanggalPergi = pickPergi.getValue();
        LocalDate tanggalDatang = pickDatang.getValue();

        try {
            String query = "{call sp_getListTiket(?, ?, ?, ?,?)}";
            CallableStatement stmt = db.conn.prepareCall(query);

            stmt.setString(1, null);

            if (asal != null) {
                stmt.setString(2, asal);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }

            if (tujuan != null) {
                stmt.setString(3, tujuan);
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            }

            if (tanggalPergi != null) {
                stmt.setDate(4, java.sql.Date.valueOf(tanggalPergi));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }

            if (tanggalDatang != null) {
                stmt.setDate(5, java.sql.Date.valueOf(tanggalDatang));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
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
                        rs.getInt("Jumlah_Tiket")
                ));
            }

            rs.close();
            stmt.close();

        } catch (Exception ex) {
            System.out.println("Terjadi error saat load data Tiket: " + ex.getMessage());
            ex.printStackTrace();
        }
        return tkList;
    }

    private void setMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > maxLength) {
                textField.setText(oldVal); // atau pakai substring kalau mau auto potong
                // textField.setText(newVal.substring(0, maxLength));
            }
        });
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

            String query = "SELECT ID_Pembelian_Tiket FROM trsPembelian ORDER BY ID_Pembelian_Tiket DESC;";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            if (!db.result.next()) {
                // Tidak ada data sama sekali
                ID = "PT" + tanggalHariIni + "-0001";
            } else {
                String lastId = db.result.getString("ID_Pembelian_Tiket");
                String lastTanggal = lastId.substring(2, 10); // Ambil bagian YYYYMMDD dari ID lama

                if (!lastTanggal.equals(tanggalHariIni)) {
                    // Jika tanggal berbeda, mulai dari 0001 lagi
                    ID = "PT" + tanggalHariIni + "-0001";
                } else {
                    String angka = lastId.split("-")[1]; // Ambil angka setelah tanda '-'
                    int prefix = Integer.parseInt(angka);
                    ID = "PT" + tanggalHariIni + "-" + String.format("%04d", prefix + 1);
                }
            }

        } catch (SQLException e) {
            System.out.println("GAGAL: " + e.getMessage());
        }
        ID_Pembelian = ID;
    }

    private String SelectNIKByUsername(String Username) {
        try {
            String query = "SELECT * FROM UserGojalan WHERE Username = 'tinaAdmin' AND Status_User = 1";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);


            if (db.result.next()) {
               return NIK = db.result.getString("NIK_User");

            }else{
                return "NIK User Tidak Ada";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return NIK;
    }

    private void getIDKursi(){
        for (String noKursi : listNomorKursi) {
            System.out.println("No Kursi: " + noKursi);
        }
    }

    @FXML
    private void onBtnSimpan(){

        if (textNama.getText() == null || textNama.getText().isEmpty() || textNama.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : Nama Tidak Boleh Kosong ! ");
            alert.initOwner(lblTotal.getScene().getWindow());
            alert.showAndWait();
            return;
        }

        if (textAlamat.getText() == null || textAlamat.getText().isEmpty() || textAlamat.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : Alamat Tidak Boleh Kosong ! ");
            alert.initOwner(lblTotal.getScene().getWindow());
            alert.showAndWait();
            return;
        }

        if (textNoTelepon.getText() == null || textNoTelepon.getText().isEmpty() || textNoTelepon.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : No Telepon Tidak Boleh Kosong ! ");
            alert.initOwner(lblTotal.getScene().getWindow());
            alert.showAndWait();
            return;
        }

        if (vbSelectedTiket == null || vbSelectedTiket.getChildren().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal: Tiket tidak boleh kosong.");
            alert.initOwner(lblTotal.getScene().getWindow());
            alert.showAndWait();
            return;
        }


        mengambilID();
        for (String IDKursi : listNomorKursi) {
            System.out.println("ID Kursi: " + IDKursi);
        }
        try {
            NIK = SelectNIKByUsername(usr);
            ID_Tiket = tiket.getID_Tiket();
            No_Telepon = textNoTelepon.getText();
            Nama = textNama.getText();
            Alamat = textAlamat.getText();
            CreatedBy = usr;

            System.out.println(ID_Pembelian);
            System.out.println(NIK);
            System.out.println(ID_Tiket);
            System.out.println(No_Telepon);
            System.out.println(Nama);
            System.out.println(totalHarga);

            // Panggil SP
            String query = "{call Sp_InsertPembelian(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement stmt = db.conn.prepareCall(query);

            stmt.setString(1, ID_Pembelian);
            stmt.setString(2, NIK);
            stmt.setString(3, ID_Tiket);
            stmt.setString(4, Nama);
            stmt.setString(5, No_Telepon);
            stmt.setString(6, Alamat);
            stmt.setDouble(7, totalHarga);
            stmt.setString(8, CreatedBy);
            stmt.setInt(9, statusPembelian );


            stmt.execute();
            stmt.close();


        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : " + ex.getMessage());
            alert.initOwner(lblTotal.getScene().getWindow());
            alert.showAndWait();
            ex.printStackTrace();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Gagal : " + ex.getMessage());
            alert.initOwner(lblTotal.getScene().getWindow());
            alert.showAndWait();
            ex.printStackTrace();
        }


            try{
                for (String IDKursi : listNomorKursi) {
                    String query = "{call Sp_InsertDetailPembelianTiket(?, ?, ?, ?, ?, ?)}";
                    CallableStatement stmt = db.conn.prepareCall(query);
                    stmt.setString(1, ID_Pembelian);
                    stmt.setString(2, IDKursi);
                    stmt.setInt(3, statusKursi);
                    stmt.setDouble(4, persenKembali);
                    stmt.setDouble(5, totalPengembalian);
                    if (TglPengembalian != null) {
                        stmt.setDate(6, java.sql.Date.valueOf(TglPengembalian));
                    } else {
                        stmt.setNull(6, java.sql.Types.DATE);
                    }
                    stmt.execute();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Berhasil");
                alert.setHeaderText(null);
                alert.setContentText("Data berhasil ditambahkan!");
                alert.initOwner(lblTotal.getScene().getWindow());
                alert.showAndWait();
                refresh();

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Gagal");
                alert.setHeaderText(null);
                alert.setContentText("Gagal : " + e.getMessage());
                alert.initOwner(lblTotal.getScene().getWindow());
                alert.showAndWait();
                e.printStackTrace();
            }
        loadTiket();
    }

    @FXML
    public void refresh() {
        textNama.clear();
        textNoTelepon.clear();
        textAlamat.clear();

        // 3. Kosongkan label total harga
        lblTotal.setText("");

        // 4. Kosongkan VBox pilihan kursi yang sudah dipilih
        vbSelectedTiket.getChildren().clear();

        // 5. Kosongkan list kursi yang dipilih
        listNomorKursi.clear();

        // 6. Kosongkan ComboBox rute dan tanggal (opsional)
        cbDari.getSelectionModel().clearSelection();
        cbKe.getSelectionModel().clearSelection();
        pickPergi.setValue(null);
        pickDatang.setValue(null);

    }

    @FXML
    public void onBtnBatal() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi");
        alert.initOwner(lblTotal.getScene().getWindow());
        alert.setHeaderText("Apakah Anda yakin ingin membatalkan?");
        alert.setContentText("Semua data yang sudah diisi akan dikosongkan.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Jika pilih OK, maka kosongkan semua
            textNama.clear();
            textNoTelepon.clear();
            textAlamat.clear();

            lblTotal.setText("");

            vbSelectedTiket.getChildren().clear();

            listNomorKursi.clear();
            lblTotalRow.setText(0 + "Tiket Ditemukan");

            cbDari.getSelectionModel().clearSelection();
            cbKe.getSelectionModel().clearSelection();
            pickPergi.setValue(null);
            pickDatang.setValue(null);
        }

        inableAllBtnTambah();
        // Jika pilih Cancel, tidak melakukan apa-apa
    }





}
