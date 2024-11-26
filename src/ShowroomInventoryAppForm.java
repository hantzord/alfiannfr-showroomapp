import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ShowroomInventoryAppForm extends JFrame {
    public JTextField txtNama, txtMerk, txtHarga;
    public JComboBox<String> cbStatus;
    public JTable table;
    public JButton btnAdd, btnEdit, btnDelete, btnUploadImage;
    public DefaultTableModel tableModel;
    public String imagePath = ""; // Variabel untuk menyimpan path gambar
    public JButton btnPrintReport;

    public ShowroomInventoryAppForm() {
        setTitle("Manajemen Showroom Inventory");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Panel for Input Fields
        JPanel panelInput = new JPanel(new GridLayout(6, 2, 10, 10)); // Menambahkan 1 baris untuk image upload
        panelInput.add(new JLabel("Nama Mobil:"));
        txtNama = new JTextField();
        panelInput.add(txtNama);

        panelInput.add(new JLabel("Merk:"));
        txtMerk = new JTextField();
        panelInput.add(txtMerk);

        panelInput.add(new JLabel("Harga:"));
        txtHarga = new JTextField();
        panelInput.add(txtHarga);

        panelInput.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"tersedia", "terjual", "booking"});
        panelInput.add(cbStatus);

        // Button Upload Image
        btnUploadImage = new JButton("Upload Image");
        panelInput.add(new JLabel("Foto Mobil:"));
        panelInput.add(btnUploadImage);

        // Buttons
        btnAdd = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Hapus");

        JPanel panelButtons = new JPanel();
        panelButtons.add(btnAdd);
        panelButtons.add(btnEdit);
        panelButtons.add(btnDelete);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Merk", "Harga", "Status", "Foto"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Layout
        add(panelInput, BorderLayout.NORTH);
        add(panelButtons, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        btnPrintReport = new JButton("Cetak Laporan");
        panelButtons.add(btnPrintReport);
    }
}
