package com.example.gojalan;

import DBConnect.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


// ✅ BENAR


public class LoginController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String validation;


    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button btnMasuk;
    @FXML
    private Label validasiLogin;

    public String user = "";
    public String pass = "";
    public String nama = "";
    public String IDSetingdb = "";
    public String usernamedb = "";


    @FXML
    public void login() {
//        switchToDashboardKepalaTravel();
        user = username.getText();
        pass = password.getText();

        try {
            DBConnect connection = new DBConnect();
            String query = "{call Sp_Login(?, ?)}";

            CallableStatement cstmt = connection.conn.prepareCall(query);
            cstmt.setString(1, user);
            cstmt.setString(2, pass);

            ResultSet rs = cstmt.executeQuery();

            if (rs.next()) {
                usernamedb = rs.getString("Username");
                nama = rs.getString("Nama");
                IDSetingdb = rs.getString("Value");
                System.out.println("IDSetingdb: " + IDSetingdb);

            }

            validasiLogin.setVisible(true);
            validasiLogin.setStyle("-fx-text-fill: white;");
            validasiLogin.setStyle("-fx-background-color: #0af564");
            validasiLogin.setStyle("-fx-background-radius: 15;");
            validation = "Selamat Datang ! ";
            showSuccessMessage(validation);

            rs.close();
            cstmt.close();
            connection.conn.close();



            if (IDSetingdb.equals("Agen")) {
                switchToDashboardAgen();
            }else if (IDSetingdb.equals("Pendataan Bus")) {
                switchToDashboardPendataan();
            } else if (IDSetingdb.equals("Kepala Travel")) {
                switchToDashboardKepalaTravel();
            } else {
                System.out.println("ID_Setting tidak dikenali: " + IDSetingdb);
            }



        } catch (SQLException ex) {
            showFailedMessage(ex.getMessage());
            validasiLogin.setStyle("-fx-background-color: red; -fx-background-radius: 15;");
            validasiLogin.setText("Login gagal: " + ex.getMessage());
        }

    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Berhasil");
        alert.setHeaderText(null);
        alert.setContentText(message);


        alert.initOwner(btnMasuk.getScene().getWindow()); // Supaya alert tidak fullscreen


        alert.initModality(Modality.WINDOW_MODAL); // Modal terhadap window pemilik saja
        alert.showAndWait();
    }

    private void showFailedMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Ganti jadi ERROR biar lebih informatif
        alert.setTitle("Gagal");
        alert.setHeaderText(null);
        alert.setContentText(message);


        alert.initOwner(btnMasuk.getScene().getWindow());


        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }

    @FXML
    private void handleMouseEnter() {
        btnMasuk.setTextFill(Color.BLUEVIOLET);
    }

    @FXML
    private void handleMouseExit() {
        btnMasuk.setTextFill(Color.BLUE);
    }

    public void switchToDashboardAgen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/gojalan/Agen/DashboardAgen.fxml"));
            Parent root = fxmlLoader.load();
            DashboardAgenController controller = fxmlLoader.getController();
            controller.setData(nama,usernamedb);
            controller.loadPage("/com/example/gojalan/Agen/HalamanAwal.fxml");
            Stage stage = (Stage) btnMasuk.getScene().getWindow(); // ✅ ambil stage dari tombol
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToDashboardPendataan() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/gojalan/PendataBus/DashboardPendataBus.fxml"));
            Parent root = fxmlLoader.load();
            DashboardPendataController controller = fxmlLoader.getController();
            controller.setData(nama,usernamedb);
            controller.loadPage("/com/example/gojalan/PendataBus/HalamanAwal.fxml");
            Stage stage = (Stage) btnMasuk.getScene().getWindow(); // ✅ ambil stage dari tombol
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToDashboardKepalaTravel() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/gojalan/KepalaTravel/DashboardKepalaTravel.fxml"));
            Parent root = fxmlLoader.load();
            DashboardKepalaController controller = fxmlLoader.getController();
            controller.setData(nama,usernamedb);
            controller.loadPage("/com/example/gojalan/KepalaTravel/HalamanAwal.fxml");
            Stage stage = (Stage) btnMasuk.getScene().getWindow(); // ✅ ambil stage dari tombol
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}