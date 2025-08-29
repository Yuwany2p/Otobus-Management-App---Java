package Jadwal;

import DBConnect.DBConnect;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class JadwalBusController implements Initializable {

    @FXML
    private VBox vbJadwal;

    DBConnect db = new DBConnect();

    public void loadJadwalBus() {
        vbJadwal.getChildren().clear(); // kosongkan dulu konten sebelumnya

        List<JadwalBus> daftarJadwalBus = getAllJadwalBus(); // method buatanmu untuk ambil data dari DB

        try {
            for (JadwalBus jb : daftarJadwalBus) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Card/cardJadwalBus.fxml"));
                Node cardNode = loader.load();

                // ambil controller-nya
                cardJadwalBusController controller = loader.getController();
                controller.setData(jb); // kirim data Tiket ke tampilan


                // tambahkan ke VBox
                vbJadwal.getChildren().add(cardNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Gagal memuat tiket: " + e.getMessage());
        }
    }

    public List<JadwalBus> getAllJadwalBus() {
        List<JadwalBus> jadwalBusList = new ArrayList<>();
        try {
            // Ambil daftar ID_Kursi dan No_Kursi untuk transaksi tertentu
            String query = "SELECT \n" +
                    "\tROW_NUMBER() OVER (ORDER BY t.ID_Bus ASC) AS No,\n" +
                    "    t.ID_Bus,                 \n" +
                    "    b.Nomor_Polisi,           \n" +
                    "    t.ID_Rute,                \n" +
                    "    r.Rute_Asal,               \n" +
                    "    r.Rute_Tujuan,\n" +
                    "\tt.Keberangkatan\n" +
                    "FROM Tiket t \n" +
                    "JOIN Bus b ON t.ID_Bus = b.ID_Bus     \n" +
                    "JOIN Rute r ON t.ID_Rute = r.ID_Rute   \n" +
                    "WHERE t.Status = 1 ";

            PreparedStatement ps = db.conn.prepareStatement(query);


            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                jadwalBusList.add(new JadwalBus(
                        rs.getInt("No"),
                        rs.getString("ID_Bus"),
                        rs.getString("Nomor_Polisi"),
                        rs.getString("ID_Rute"),
                        rs.getString("Rute_Asal"),
                        rs.getString("Rute_Tujuan"),
                        rs.getString("Keberangkatan")
                        ));
            }
            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jadwalBusList;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadJadwalBus();
    }
}
