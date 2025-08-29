package Laporan;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;


import DBConnect.DBConnect;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.UnaryOperator;

public class LaporanController {

    @FXML
    private BarChart<String, Number> barChartLaporan;

    @FXML
    private NumberAxis yAxisLaporan;

    @FXML
    private CategoryAxis xAxisLaporan;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnExportPDF;

    @FXML
    private ComboBox<String> cbTahun;

    @FXML
    private Button btnRefresh;

    @FXML
    private DatePicker dateDari;

    @FXML
    private DatePicker dateSampai;

    @FXML
    private Label lblKeuntungan;

    @FXML
    private Label lblPembatalan;

    DBConnect db = new DBConnect();
    String username;
    String nama;
    String jabatan;


    @FXML
    public void initialize() {
        loadChartData();
        updateTotalKeuangan();
        loadComboBoxTahun();
        yAxisLaporan.setTickUnit(1);
        yAxisLaporan.setMinorTickVisible(false);
        yAxisLaporan.setForceZeroInRange(true);
        cbTahun.setValue(String.valueOf(LocalDate.now().getYear()));

        xAxisLaporan.setTickLabelFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));
        barChartLaporan.lookup(".chart-title").setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        btnRefresh.setOnAction(e -> {
            loadChartData();
            updateTotalKeuangan();
        });

        dateDari.setOnAction(e -> {
            loadChartData();
            updateTotalKeuangan();
        });

        dateSampai.setOnAction(e -> {
            loadChartData();
            updateTotalKeuangan();
        });

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9/]*")) {
                return change;
            }
            return null;
        };

        dateDari.getEditor().setTextFormatter(new TextFormatter<>(filter));
        dateSampai.getEditor().setTextFormatter(new TextFormatter<>(filter));
    }

    public void setNama(String usr, String nama){
        this.username = usr;
        this.nama = nama;
    }

    private void loadComboBoxTahun() {
        Set<String> tahunSet = new TreeSet<>(); // pakai TreeSet biar otomatis urut
        String sql = "SELECT DISTINCT YEAR(Tgl_Pembelian) AS Tahun FROM trsPembelian ORDER BY Tahun DESC";

        try {
            if (db.conn == null || db.conn.isClosed()) db = new DBConnect();
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tahunSet.add(rs.getString("Tahun")); // simpan sebagai string langsung
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cbTahun.getItems().clear();
        cbTahun.getItems().addAll(tahunSet);

        // Optional: set default tahun ke tahun sekarang jika tersedia
        String tahunSekarang = String.valueOf(LocalDate.now().getYear());
        if (tahunSet.contains(tahunSekarang)) {
            cbTahun.setValue(tahunSekarang);
        } else if (!tahunSet.isEmpty()) {
            cbTahun.setValue(tahunSet.iterator().next()); // fallback ke tahun pertama
        }
    }

    private void loadChartData() {
        LocalDate dari = dateDari.getValue();
        LocalDate sampai = dateSampai.getValue();

        XYChart.Series<String, Number> seriesPembelian = new XYChart.Series<>();
        seriesPembelian.setName("Pembelian Tiket");

        XYChart.Series<String, Number> seriesPembatalan = new XYChart.Series<>();
        seriesPembatalan.setName("Pembatalan Tiket");

        if (dari != null && sampai != null) {
            Map<String, Integer> dataPembelian = getJumlahTransaksiPerTanggal(dari, sampai);
            Map<String, Integer> dataPembatalan = getJumlahPembatalanPerTanggal(dari, sampai);

            Set<String> semuaTanggal = new TreeSet<>(dataPembelian.keySet());
            semuaTanggal.addAll(dataPembatalan.keySet());

            for (String tgl : semuaTanggal) {
                int pembelian = dataPembelian.getOrDefault(tgl, 0);
                int pembatalan = dataPembatalan.getOrDefault(tgl, 0);

                seriesPembelian.getData().add(new XYChart.Data<>(tgl, pembelian));
                seriesPembatalan.getData().add(new XYChart.Data<>(tgl, pembatalan));
            }

        } else {
            Map<String, Integer> pembelianPerBulan = getJumlahPembelianPerBulan();
            Map<String, Integer> pembatalanPerBulan = getJumlahPembatalanPerBulan();

            for (int i = 1; i <= 12; i++) {
                String bulan = LocalDate.of(2000, i, 1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                int pembelian = pembelianPerBulan.getOrDefault(bulan.toUpperCase(), 0);
                int pembatalan = pembatalanPerBulan.getOrDefault(bulan.toUpperCase(), 0);

                seriesPembelian.getData().add(new XYChart.Data<>(bulan, pembelian));
                seriesPembatalan.getData().add(new XYChart.Data<>(bulan, pembatalan));
            }
        }

        barChartLaporan.getData().clear();
        barChartLaporan.getData().addAll(seriesPembelian, seriesPembatalan);
    }

    private Map<String, Integer> getJumlahTransaksiPerTanggal(LocalDate dari, LocalDate sampai) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT CONVERT(VARCHAR, Tgl_Pembelian, 23) AS Tanggal, COUNT(*) AS JumlahTransaksi " +
                "FROM trsPembelian WHERE Status_Pembelian = 1 AND Tgl_Pembelian BETWEEN ? AND ? " +
                "GROUP BY CONVERT(VARCHAR, Tgl_Pembelian, 23) ORDER BY Tanggal";

        try {
            if (db.conn == null || db.conn.isClosed()) db = new DBConnect();
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(dari));
            ps.setDate(2, java.sql.Date.valueOf(sampai));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("Tanggal"), rs.getInt("JumlahTransaksi"));
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private Map<String, Integer> getJumlahPembatalanPerTanggal(LocalDate dari, LocalDate sampai) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT CONVERT(VARCHAR, Tanggal_Pembatalan, 23) AS Tanggal, COUNT(*) AS JumlahPembatalan " +
                "FROM detailPembelianTiket WHERE Tanggal_Pembatalan BETWEEN ? AND ? " +
                "GROUP BY CONVERT(VARCHAR, Tanggal_Pembatalan, 23) ORDER BY Tanggal";

        try {
            if (db.conn == null || db.conn.isClosed()) db = new DBConnect();
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(dari));
            ps.setDate(2, java.sql.Date.valueOf(sampai));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("Tanggal"), rs.getInt("JumlahPembatalan"));
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private Map<String, Integer> getJumlahPembatalanPerBulan() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT DATENAME(MONTH, Tanggal_Pembatalan) AS Bulan, COUNT(*) AS JumlahPembatalan " +
                "FROM detailPembelianTiket WHERE Tanggal_Pembatalan IS NOT NULL GROUP BY DATENAME(MONTH, Tanggal_Pembatalan), MONTH(Tanggal_Pembatalan) ORDER BY MONTH(Tanggal_Pembatalan)";

        try {
            if (db.conn == null || db.conn.isClosed()) db = new DBConnect();
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("Bulan").toUpperCase(), rs.getInt("JumlahPembatalan"));
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private Map<String, Integer> getJumlahPembelianPerBulan() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT DATENAME(MONTH, Tgl_Pembelian) AS Bulan, COUNT(*) AS JumlahPembelian " +
                "FROM trsPembelian WHERE Status_Pembelian = 1 GROUP BY DATENAME(MONTH, Tgl_Pembelian), MONTH(Tgl_Pembelian) ORDER BY MONTH(Tgl_Pembelian)";

        try {
            if (db.conn == null || db.conn.isClosed()) db = new DBConnect();
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("Bulan").toUpperCase(), rs.getInt("JumlahPembelian"));
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private Map<Integer, Integer> getPembelianPerBulan() {
        Map<Integer, Integer> map = new HashMap<>();
        String sql = "SELECT MONTH(Tgl_Pembelian) AS Bulan, COUNT(*) AS TotalPembelian " +
                "FROM trsPembelian WHERE Status_Pembelian = 1 " +
                "GROUP BY MONTH(Tgl_Pembelian) ORDER BY Bulan";

        try {
            if (db.conn == null || db.conn.isClosed()) db = new DBConnect();
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int bulan = rs.getInt("Bulan"); // ← angka bulan
                int total = rs.getInt("TotalPembelian");
                map.put(bulan, total);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    private Map<Integer, Integer> getPembatalanPerBulan() {
        Map<Integer, Integer> map = new HashMap<>();
        String sql = "SELECT MONTH(Tanggal_Pembatalan) AS Bulan, COUNT(*) AS TotalPembatalan " +
                "FROM detailPembelianTiket WHERE Tanggal_Pembatalan IS NOT NULL " +
                "GROUP BY MONTH(Tanggal_Pembatalan) ORDER BY Bulan";

        try {
            if (db.conn == null || db.conn.isClosed()) db = new DBConnect();
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int bulan = rs.getInt("Bulan");
                int total = rs.getInt("TotalPembatalan");
                map.put(bulan, total);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }


    public void loadTotalPembelianDanPembatalan(Label lblPembelian, Label lblPembatalan) {
        try {
            String sql = "SELECT * FROM viewTotalPembelianDanPembatalan";
            PreparedStatement ps = db.conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblPembelian.setText(String.format("Rp %,.0f", rs.getDouble("TotalPembelian")));
                lblPembatalan.setText(String.format("Rp %,.0f", rs.getDouble("TotalPembatalan")));
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotalKeuangan() {
        LocalDate dari = dateDari.getValue();
        LocalDate sampai = dateSampai.getValue();

        double totalPembelian = 0;
        double totalPembatalan = 0;

        if (dari != null && sampai != null) {
            String sql = "SELECT SUM(Total_Harga) AS TotalPembelian FROM trsPembelian WHERE Status_Pembelian = 1 AND Tgl_Pembelian BETWEEN ? AND ?";
            String sql2 = "SELECT SUM(Total_Pengembalian) AS TotalPembatalan FROM detailPembelianTiket WHERE Tanggal_Pembatalan BETWEEN ? AND ?";

            try {
                if (db.conn == null || db.conn.isClosed()) db = new DBConnect();

                PreparedStatement ps1 = db.conn.prepareStatement(sql);
                ps1.setDate(1, java.sql.Date.valueOf(dari));
                ps1.setDate(2, java.sql.Date.valueOf(sampai));
                ResultSet rs1 = ps1.executeQuery();

                if (rs1.next()) totalPembelian = rs1.getDouble("TotalPembelian");
                rs1.close();
                ps1.close();

                PreparedStatement ps2 = db.conn.prepareStatement(sql2);
                ps2.setDate(1, java.sql.Date.valueOf(dari));
                ps2.setDate(2, java.sql.Date.valueOf(sampai));
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) totalPembatalan = rs2.getDouble("TotalPembatalan");
                rs2.close();
                ps2.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            lblKeuntungan.setText("Rp " + String.format("%,.0f", totalPembelian));
            lblPembatalan.setText("Rp " + String.format("%,.0f", totalPembatalan));
        } else {
            loadTotalPembelianDanPembatalan(lblKeuntungan, lblPembatalan);
        }
    }

    @FXML
    private void onBtnClear() {
        dateDari.setValue(null);     // Kosongkan tanggal dari
        dateSampai.setValue(null);   // Kosongkan tanggal sampai
        cbTahun.setValue(null);      // Kosongkan pilihan tahun
    }

    @FXML
    public void onButtonLihatLaporan() {
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put("Tanggal_Dari", java.sql.Date.valueOf(dateDari.getValue()));
            param.put("Tanggal_Sampai", java.sql.Date.valueOf(dateSampai.getValue()));

            JasperPrint jp = JasperFillManager.fillReport(
                    getClass().getResourceAsStream("/Report/Laporan _Gojalan.jasper"),
                    param,
                    db.conn
            );

            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setExtendedState(JFrame.MAXIMIZED_BOTH); // Tambahkan ini untuk fullscreen
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Laporan_Transaksi_Gojalan.pdf");

        File selectedFile = fileChooser.showSaveDialog(btnExportPDF.getScene().getWindow());
        if (selectedFile == null) return;

        String pdfFilePath = selectedFile.getAbsolutePath();
        if (!pdfFilePath.toLowerCase().endsWith(".pdf")) {
            pdfFilePath += ".pdf";
        }

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();

            // === KOP SURAT ===
            Image img = Image.getInstance("src/main/resources/Images/logoLaporan.png");
            img.scaleToFit(100, 100);
            img.setAlignment(Element.ALIGN_LEFT);

            Paragraph kop1 = new Paragraph("TRAVEL BUS GOJALAN", FontFactory.getFont("Times-Roman", 16, Font.BOLD));
            Paragraph kop2 = new Paragraph("Jl. Melati Indah No. 25, RT 04/RW 09, Kelurahan Harapan Jaya,\nKecamatan Bekasi Utara, Kota Bekasi, Jawa Barat 17123", FontFactory.getFont("Times-Roman", 12));
            Paragraph kop3 = new Paragraph("NoTelp : 08978801837     Email : kelompok7prg3@gmail.com", FontFactory.getFont("Times-Roman", 12));
            Paragraph kota = new Paragraph("KOTA CIKARANG", FontFactory.getFont("Times-Roman", 13, Font.BOLD));

            Paragraph textGroup = new Paragraph();
            textGroup.setAlignment(Element.ALIGN_CENTER);
            textGroup.add(kop1);
            textGroup.add(kop2);
            textGroup.add(kop3);
            textGroup.add(kota);

            PdfPTable kopTable = new PdfPTable(2);
            kopTable.setWidthPercentage(100);
            kopTable.setWidths(new float[]{1f, 5f});

            PdfPCell cellLogo = new PdfPCell(img, false);
            cellLogo.setBorder(Rectangle.NO_BORDER);
            cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellLogo.setVerticalAlignment(Element.ALIGN_CENTER);

            PdfPCell cellText = new PdfPCell();
            cellText.setBorder(Rectangle.NO_BORDER);
            cellText.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellText.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellText.addElement(textGroup);

            kopTable.addCell(cellLogo);
            kopTable.addCell(cellText);

            PdfPCell borderCell = new PdfPCell(new Phrase(" "));
            borderCell.setColspan(2);
            borderCell.setBorder(Rectangle.BOTTOM);
            borderCell.setPaddingTop(8f);
            kopTable.addCell(borderCell);

            document.add(kopTable);

            // === PERIODE ===
            LocalDate dari = dateDari.getValue() != null ? dateDari.getValue() : LocalDate.of(LocalDate.now().getYear(), 1, 1);
            LocalDate sampai = dateSampai.getValue() != null ? dateSampai.getValue() : LocalDate.of(LocalDate.now().getYear(), 12, 31);

            Paragraph kepada = new Paragraph("\n\nKepada :\nOwner Travel Bus Gojalan", FontFactory.getFont("Times-Roman", 12));
            Paragraph desc = new Paragraph(
                    "Berikut adalah laporan transaksi dan keuangan travel bus Gojalan\n" +
                            "periode " + dari + " – " + sampai + " :\n\n",
                    FontFactory.getFont("Times-Roman", 12)
            );
            document.add(kepada);
            document.add(desc);

            // === TABEL 1: Jumlah Tiket ===
            PdfPTable table1 = new PdfPTable(3);
            table1.setWidthPercentage(100);
            table1.setSpacingAfter(20);
            Font headerFont = FontFactory.getFont("Times-Roman", 13, Font.BOLD);
            PdfPCell cell1 = new PdfPCell(new Phrase("Tanggal/Bulan", headerFont));
            cell1.setBackgroundColor(new Color(200, 221, 242));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Jumlah Pembelian Tiket", headerFont));
            cell2.setBackgroundColor(new Color(200, 221, 242));
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Jumlah Pembatalan Tiket", headerFont));
            cell3.setBackgroundColor(new Color(200, 221, 242));
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell3);


            int totalPembelian = 0;
            int totalPembatalan = 0;
            boolean isHarian = (dateDari.getValue() != null && dateSampai.getValue() != null);

            if (isHarian) {
                Map<String, Integer> pembelianMap = getJumlahTransaksiPerTanggal(dari, sampai);
                Map<String, Integer> pembatalanMap = getJumlahPembatalanPerTanggal(dari, sampai);
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
                LocalDate tgl = dari;

                while (!tgl.isAfter(sampai)) {
                    String key = tgl.toString(); // yyyy-MM-dd
                    int pembelian = pembelianMap.getOrDefault(key, 0);
                    int pembatalan = pembatalanMap.getOrDefault(key, 0);

                    table1.addCell(tgl.format(fmt));
                    table1.addCell(String.valueOf(pembelian));
                    table1.addCell(String.valueOf(pembatalan));

                    totalPembelian += pembelian;
                    totalPembatalan += pembatalan;
                    tgl = tgl.plusDays(1);
                }

            } else {
                Map<Integer, Integer> pembelianMap = getPembelianPerBulan();
                Map<Integer, Integer> pembatalanMap = getPembatalanPerBulan();

                for (int i = 1; i <= 12; i++) {
                    String namaBulan = Month.of(i).getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
                    int pembelian = pembelianMap.getOrDefault(i, 0);
                    int pembatalan = pembatalanMap.getOrDefault(i, 0);

                    table1.addCell(namaBulan);
                    table1.addCell(String.valueOf(pembelian));
                    table1.addCell(String.valueOf(pembatalan));

                    totalPembelian += pembelian;
                    totalPembatalan += pembatalan;
                }
            }

            PdfPCell jumlahCell = new PdfPCell(new Phrase("Jumlah", headerFont));
            jumlahCell.setBackgroundColor(new Color(255, 255, 153)); // warna kuning muda
            jumlahCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            jumlahCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table1.addCell(String.valueOf(totalPembelian));
            table1.addCell(String.valueOf(totalPembatalan));
            document.add(table1);

            // === TABEL 2: Nominal Tiket ===
            PdfPTable table2 = new PdfPTable(3);
            table2.setWidthPercentage(100);
            table2.setSpacingAfter(30);
            Font headerFont2 = FontFactory.getFont("Times-Roman", 11, Font.BOLD);
            Color headerBlue = new Color(173, 216, 230); // Light blue

            PdfPCell t2cell1 = new PdfPCell(new Phrase("Tanggal/Bulan", headerFont2));
            t2cell1.setBackgroundColor(headerBlue);
            t2cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            t2cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(t2cell1);

            PdfPCell t2cell2 = new PdfPCell(new Phrase("Pembelian Tiket (Rp)", headerFont2));
            t2cell2.setBackgroundColor(headerBlue);
            t2cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            t2cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(t2cell2);

            PdfPCell t2cell3 = new PdfPCell(new Phrase("Pembatalan Tiket (Rp)", headerFont2));
            t2cell3.setBackgroundColor(headerBlue);
            t2cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            t2cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(t2cell3);


            if (isHarian) {
                String sqlPembelian = "SELECT CONVERT(VARCHAR, Tgl_Pembelian, 23) AS Tanggal, SUM(Total_Harga) AS Total FROM trsPembelian WHERE Status_Pembelian = 1 AND Tgl_Pembelian BETWEEN ? AND ? GROUP BY CONVERT(VARCHAR, Tgl_Pembelian, 23)";
                String sqlPembatalan = "SELECT CONVERT(VARCHAR, Tanggal_Pembatalan, 23) AS Tanggal, SUM(Total_Pengembalian) AS Total FROM detailPembelianTiket WHERE Tanggal_Pembatalan BETWEEN ? AND ? GROUP BY CONVERT(VARCHAR, Tanggal_Pembatalan, 23)";
                Map<String, Double> pembelianMap = new HashMap<>();
                Map<String, Double> pembatalanMap = new HashMap<>();

                double totalRpPembelian = 0;
                double totalRpPembatalan = 0;

                try {
                    PreparedStatement ps1 = db.conn.prepareStatement(sqlPembelian);
                    ps1.setDate(1, java.sql.Date.valueOf(dari));
                    ps1.setDate(2, java.sql.Date.valueOf(sampai));
                    ResultSet rs1 = ps1.executeQuery();
                    while (rs1.next()) pembelianMap.put(rs1.getString("Tanggal"), rs1.getDouble("Total"));
                    rs1.close(); ps1.close();

                    PreparedStatement ps2 = db.conn.prepareStatement(sqlPembatalan);
                    ps2.setDate(1, java.sql.Date.valueOf(dari));
                    ps2.setDate(2, java.sql.Date.valueOf(sampai));
                    ResultSet rs2 = ps2.executeQuery();
                    while (rs2.next()) pembatalanMap.put(rs2.getString("Tanggal"), rs2.getDouble("Total"));
                    rs2.close(); ps2.close();

                    LocalDate tgl = dari;
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
                    while (!tgl.isAfter(sampai)) {
                        String key = tgl.toString();
                        double val1 = pembelianMap.getOrDefault(key, 0.0);
                        double val2 = pembatalanMap.getOrDefault(key, 0.0);

                        table2.addCell(tgl.format(fmt));
                        table2.addCell(String.format("Rp %,.0f", val1));
                        table2.addCell(String.format("Rp %,.0f", val2));

                        totalRpPembelian += val1;
                        totalRpPembatalan += val2;
                        tgl = tgl.plusDays(1);
                    }

                    table2.addCell("Total");
                    table2.addCell(String.format("Rp %,.0f", totalRpPembelian));
                    table2.addCell(String.format("Rp %,.0f", totalRpPembatalan));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    String sql = "SELECT * FROM viewTotalPembelianDanPembatalan";
                    PreparedStatement ps = db.conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    double val1 = 0, val2 = 0;
                    if (rs.next()) {
                        val1 = rs.getDouble("TotalPembelian");
                        val2 = rs.getDouble("TotalPembatalan");
                    }
                    rs.close(); ps.close();

                    table2.addCell("Total");
                    table2.addCell(String.format("Rp %,.00f", val1));
                    table2.addCell(String.format("Rp %,.00f", val2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            document.add(table2);

            // === PENUTUP ===
            try {
                String sql = "SELECT u.Nama, s.Value AS Jabatan FROM UserGojalan u JOIN Setting s ON u.ID_Setting = s.ID_Setting WHERE u.Username = ? AND s.Nama = 'Jabatan'";
                PreparedStatement ps = db.conn.prepareStatement(sql);
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nama = rs.getString("Nama");
                    jabatan = rs.getString("Jabatan");
                }
                rs.close(); ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String tanggalCetak = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID")));
            Paragraph penutup = new Paragraph(
                    "Demikian Laporan Transaksi dan Keuangan Travel Bus Gojalan.\n\nTerima kasih\n\n" +
                            jabatan + "\n\n\n\n" + nama + "\n" + tanggalCetak,
                    FontFactory.getFont("Times-Roman", 11)
            );
            penutup.setSpacingBefore(20);
            document.add(penutup);

            document.close();
            System.out.println("Laporan berhasil diekspor ke: " + pdfFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}