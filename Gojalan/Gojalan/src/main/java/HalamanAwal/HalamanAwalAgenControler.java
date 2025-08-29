package HalamanAwal;

import DBConnect.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HalamanAwalAgenControler implements Initializable {

    @FXML
    private Label lbKeberangkatan;

    @FXML
    private Label lbPenumpang;

    @FXML
    private Label lbTiket;

    DBConnect db = new DBConnect();
    List<Integer> jumlah  = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        load();
    }

    public void load() {
        try {
            String query = "select * from dbo.fn_GetSummaryHariIni()";
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
        int Keberangkatan = jumlah.get(1);
        int Penumpang = jumlah.get(2);

        lbKeberangkatan.setText(String.valueOf(Keberangkatan));
        lbPenumpang.setText(String.valueOf(Penumpang));
        lbTiket.setText(String.valueOf(Tiket));
    }
}
