module com.example.gojalan {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;
    requires itext;
    requires transitive org.apache.logging.log4j;
    requires net.sf.jasperreports.core;


    opens com.example.gojalan to javafx.fxml;

    exports com.example.gojalan;
    opens Master.Setting to javafx.fxml, javafx.base;
    exports Master.User;
    opens Master.User to javafx.fxml, javafx.base;
    exports Master.Kursi;
    opens Master.Kursi to javafx.fxml, javafx.base;
    exports Master.Supir;
    opens Master.Supir to javafx.fxml, javafx.base;
    exports Master.Bus;
    opens Master.Bus to javafx.fxml, javafx.base;
    exports Master.Rute;
    opens Master.Rute to javafx.fxml, javafx.base;
    exports Master.Tiket;
    opens Master.Tiket to javafx.fxml, javafx.base;

    exports Transaksi.PengembalianTiket;
    opens Transaksi.PengembalianTiket to javafx.fxml;

    exports Transaksi.PembelianTiket;
    opens Transaksi.PembelianTiket to javafx.fxml;

    opens Jadwal to javafx.fxml;

    exports Laporan;
    opens Laporan to javafx.fxml, javafx.base;

    exports HalamanAwal;
    opens HalamanAwal to javafx.fxml;
}