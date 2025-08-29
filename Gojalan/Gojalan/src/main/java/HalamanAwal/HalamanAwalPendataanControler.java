package HalamanAwal;

import DBConnect.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HalamanAwalPendataanControler implements Initializable {

    @FXML
    private PieChart Top5Bus;

    @FXML
    private PieChart Top5Rute;

    @FXML
    private Label lbKursi;

    @FXML
    private Label lbBus;

    DBConnect db = new DBConnect();
    List<Integer> jumlah  = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tampilkanPieTopRute(); // Menampilkan Pie Chart saat halaman dibuka
        tampilkanPieTopBus();
        Label();
    }

    public List<PieChart.Data> ambilDataTopRute(String Jenis) {
        List<PieChart.Data> dataList = new ArrayList<>();
        String query ="";
        if (Jenis.equalsIgnoreCase("Rute")) {
            query = "SELECT Rute, Jumlah_Disewa, Persen FROM dbo.fn_Top5RuteTerlaku()";
        }
        else {
            query = "select Nama_Bus, Jumlah_Disewa, Persen from dbo.fn_Top5BusTerlaku()";
        }

        try {
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            while (db.result.next()) {
                String rute = db.result.getString(Jenis);
                int jumlah = (int) db.result.getDouble("Jumlah_Disewa");
                double persen = db.result.getDouble("Persen");
                dataList.add(new PieChart.Data(rute + " (" + jumlah + ") "+persen+"%", jumlah));
            }

            db.result.close();
            db.stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataList;
    }


    public void tampilkanPieTopRute() {
        List<PieChart.Data> pieData = ambilDataTopRute("Rute");
        ObservableList<PieChart.Data> observableData = FXCollections.observableArrayList(pieData);

        Top5Rute.setData(observableData);
        Top5Rute.setTitle("Top 5 Rute Tiket");

        // Tambahkan warna slice langsung via Java
        String[] warnaSlice = {
                "#3498db", // Biru
                "#2ecc71", // Hijau
                "#f1c40f", // Kuning
                "#e67e22", // Oranye
                "#e74c3c"  // Merah
        };

        int index = 0;
        for (PieChart.Data data : Top5Rute.getData()) {
            String warna = warnaSlice[index % warnaSlice.length]; // Hindari IndexOutOfBounds
            data.getNode().setStyle("-fx-pie-color: " + warna + ";");
            index++;
        }
    }

    public void tampilkanPieTopBus() {
        List<PieChart.Data> pieData = ambilDataTopRute("Nama_Bus");
        ObservableList<PieChart.Data> observableData = FXCollections.observableArrayList(pieData);

        Top5Bus.setData(observableData);
        Top5Bus.setTitle("Top 5 Bus Tiket");

        // Tambahkan warna slice langsung via Java
        String[] warnaSlice = {
                "#3498db", // Biru
                "#2ecc71", // Hijau
                "#f1c40f", // Kuning
                "#e67e22", // Oranye
                "#e74c3c"  // Merah
        };

        int index = 0;
        for (PieChart.Data data : Top5Bus.getData()) {
            String warna = warnaSlice[index % warnaSlice.length]; // Hindari IndexOutOfBounds
            data.getNode().setStyle("-fx-pie-color: " + warna + ";");
            index++;
        }
    }

    public void Label() {
        try {
            String query = "select * from dbo.fn_GetKursiDanBusAktif()";
            db.stat = db.conn.createStatement();
            db.result = db.stat.executeQuery(query);

            while (db.result.next()) {
                jumlah.add(db.result.getInt("Jumlah"));
            }
            db.stat.close();
            db.result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int Kursi = jumlah.get(0);
        int Bus = jumlah.get(1);
//        int Penumpang = jumlah.get(2);

        lbKursi.setText(String.valueOf(Kursi));
        lbBus.setText(String.valueOf(Bus));
    }
}
