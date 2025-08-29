package HalamanAwal;

import DBConnect.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HalamanAwalKepala implements Initializable {

    @FXML
    private BarChart<String, Number> chartPembelian;

    @FXML
    private Label lbTiket;

    @FXML
    private Label lbUser;

    DBConnect db = new DBConnect();
    List<Integer> jumlah  = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadChartPembelian(2025); // Ganti dengan tahun dinamis jika perlu
        Label();
    }

    public void loadChartPembelian(int tahun) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tiket Terjual");

        try {
            String query = "SELECT Nama_Bulan, Jumlah_Tiket_Terjual FROM dbo.fn_RekapTiketTerjualPerBulan(?) ORDER BY Bulan";
            PreparedStatement stmt = db.conn.prepareStatement(query);
            stmt.setInt(1, tahun);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bulan = rs.getString("Nama_Bulan");
                int jumlah = rs.getInt("Jumlah_Tiket_Terjual");

                XYChart.Data<String, Number> data = new XYChart.Data<>(bulan, jumlah);
                series.getData().add(data);
            }

            chartPembelian.getData().clear();
            chartPembelian.getData().add(series);

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.out.println("Gagal memuat data chart: " + e.getMessage());
        }
    }

    public void Label() {
        try {
            String query = "SELECT * FROM dbo.fn_GetTiketDanUser()";
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

        int Tiket = jumlah.get(0);
        int user = jumlah.get(1);
//        int Penumpang = jumlah.get(2);

        lbTiket.setText(String.valueOf(Tiket));
        lbUser.setText(String.valueOf(user));
    }
}