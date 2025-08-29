package com.example.gojalan;

import DBConnect.DBConnect;
import com.example.gojalan.DashboardKepalaController;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.sql.CallableStatement;
import java.sql.SQLException;

public class EditUserController {

    @FXML
    private Button btnBatal;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnSimpan;

    @FXML
    private PasswordField oldPassword, newPassword, confirmPassword;

    @FXML
    private Label lblTitle;

    DBConnect db = new DBConnect();
    String username;


    public void setUsername(String username) {
        this.username  = username;
    }

    @FXML
    private void onBatalClick() {
        oldPassword.setText("");
        newPassword.setText("");
        confirmPassword.setText("");
    }

    @FXML
    private void onExitClick() {
        Scene scene = oldPassword.getScene();
        if (scene != null && scene.getUserData() instanceof DashboardKepalaController controller) {
            controller.closePopup1(); // Panggil popup close dari controller utama
        }
        if (scene != null && scene.getUserData() instanceof DashboardPendataController controller) {
            controller.closePopup1(); // Panggil popup close dari controller utama
        }
        if (scene != null && scene.getUserData() instanceof DashboardAgenController controller) {
            controller.closePopup1(); // Panggil popup close dari controller utama
        }
    }

    @FXML
    private void onSimpanClick() {
        String oldPwd = oldPassword.getText();
        String newPwd = newPassword.getText();
        String confirmPwd = confirmPassword.getText();
        confirmPassword.setStyle("");
        oldPassword.setStyle("");

        // Validasi input
        if (!newPwd.isEmpty() && !confirmPwd.isEmpty()) {
            if (!newPwd.equals(confirmPwd)) {
                confirmPassword.setStyle("-fx-border-color: red");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Gagal");
                alert.setHeaderText(null);
                alert.setContentText("Password tidak sama!");
                alert.initOwner(lblTitle.getScene().getWindow());
                alert.showAndWait();
                return;
            }

            // Panggil stored procedure
            try {
                String query = "{call Sp_UpdatePasswordByUsername(?, ?, ?, ?)}";

                CallableStatement stmt = db.conn.prepareCall(query);
                stmt.setString(1, username);
                stmt.setString(2, oldPwd);
                stmt.setString(3, newPwd);
                stmt.setString(4, username);

                stmt.execute();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Berhasil");
                alert.setHeaderText(null);
                alert.setContentText("Password berhasil diubah.");
                alert.initOwner(lblTitle.getScene().getWindow());
                alert.showAndWait();
                onBatalClick();
                // Tutup popup jika berhasil
                Scene scene = newPassword.getScene();
                if (scene != null && scene.getUserData() instanceof DashboardKepalaController controller) {
                    controller.closePopup1();
                }


            } catch (SQLException e) {
                oldPassword.setStyle("-fx-border-color: red");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Kesalahan");
                alert.setHeaderText(null);
                alert.setContentText("Gagal mengubah password: " + e.getMessage());
                alert.initOwner(lblTitle.getScene().getWindow());
                alert.showAndWait();
            }
        }

    }
}
