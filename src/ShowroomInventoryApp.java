import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShowroomInventoryApp {
    private ShowroomInventoryAppForm form;
    private Connection connection;

    public ShowroomInventoryApp() {
        form = new ShowroomInventoryAppForm();
        connectToDatabase();
        loadData();

        // Add Action Listeners for buttons
        form.btnAdd.addActionListener(e -> addMobil());
        form.btnEdit.addActionListener(e -> editMobil());
        form.btnDelete.addActionListener(e -> deleteMobil());
        form.btnUploadImage.addActionListener(e -> uploadImage());
        form.btnPrintReport.addActionListener(e -> printReport());

        form.table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = form.table.getSelectedRow();
                form.txtNama.setText((String) form.tableModel.getValueAt(selectedRow, 1));
                form.txtMerk.setText((String) form.tableModel.getValueAt(selectedRow, 2));
                form.txtHarga.setText(form.tableModel.getValueAt(selectedRow, 3).toString().replace("Rp", "").replace(",", "").trim());
                form.cbStatus.setSelectedItem((String) form.tableModel.getValueAt(selectedRow, 4));
            }
        });

        form.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/showroom", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(form, "Koneksi ke database gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void loadData() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM mobil_inventory");

            form.tableModel.setRowCount(0);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            while (rs.next()) {
                String fotoPath = rs.getString("foto");
                ImageIcon imageIcon = new ImageIcon(fotoPath);
                Image image = imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(image);

                form.tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("merk"),
                        currencyFormat.format(rs.getDouble("harga")),
                        rs.getString("status"),
                        imageIcon
                });
            }

            form.table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    if (value instanceof ImageIcon) {
                        return new JLabel((ImageIcon) value);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMobil() {
        String nama = form.txtNama.getText();
        String merk = form.txtMerk.getText();
        String hargaString = form.txtHarga.getText().replaceAll("[^\\d.]", "");
        double harga = Double.parseDouble(hargaString);
        String status = (String) form.cbStatus.getSelectedItem();
        String fotoPath = form.imagePath;

        try {
            String sql = "INSERT INTO mobil_inventory (nama, merk, harga, status, foto) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nama);
            pstmt.setString(2, merk);
            pstmt.setDouble(3, harga);
            pstmt.setString(4, status);
            pstmt.setString(5, fotoPath);
            pstmt.executeUpdate();

            loadData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editMobil() {
        int selectedRow = form.table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(form, "Pilih mobil yang ingin diedit terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) form.tableModel.getValueAt(selectedRow, 0);
        String nama = form.txtNama.getText();
        String merk = form.txtMerk.getText();
        String hargaString = form.txtHarga.getText().replaceAll("[Rp,.]", "").trim();
        double harga = Double.parseDouble(hargaString);
        String status = (String) form.cbStatus.getSelectedItem();
        String fotoPath = form.imagePath;

        try {
            String sql = "UPDATE mobil_inventory SET nama=?, merk=?, harga=?, status=?, foto=? WHERE id=?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nama);
            pstmt.setString(2, merk);
            pstmt.setDouble(3, harga);
            pstmt.setString(4, status);
            pstmt.setString(5, fotoPath);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();

            loadData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteMobil() {
        int selectedRow = form.table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(form, "Pilih mobil yang ingin dihapus terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) form.tableModel.getValueAt(selectedRow, 0);

        try {
            String sql = "DELETE FROM mobil_inventory WHERE id=?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            loadData();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Foto Mobil");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
        int result = fileChooser.showOpenDialog(form);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            form.imagePath = "images/" + selectedFile.getName();
            JOptionPane.showMessageDialog(form, "Foto berhasil dipilih!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void printReport() {
        try {
            String reportPath = getClass().getResource("/reportShowroom.jrxml").getPath();
            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, parameters, connection);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        form.txtNama.setText("");
        form.txtMerk.setText("");
        form.txtHarga.setText("");
        form.cbStatus.setSelectedIndex(0);
        form.imagePath = "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShowroomInventoryApp());
    }
}
