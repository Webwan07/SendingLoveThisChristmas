package myclass;

import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;

public class Helper {
    public static String dateFormat = "yyyy-MM-dd";
    
    public static Object get_AddedDate(JDateChooser g_date){
        SimpleDateFormat dateFormats = new SimpleDateFormat(dateFormat);
        if(g_date.getDate() != null){
            return dateFormats.format(g_date.getDate());
        }
        return java.sql.Date.valueOf(LocalDate.now());
    }
    
    public static String getNewValTable(javax.swing.JTable table){
        int editedRow = table.getEditingRow();
        int editedColumn = table.getEditingColumn();
        
        table.editCellAt(table.getSelectedRow(), table.getSelectedColumn());
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        String editedValue = null;
        
        if (editedRow != -1 && editedColumn != -1) {
            TableCellEditor editor = table.getCellEditor(editedRow, editedColumn);
            if (editor != null) {
                editor.stopCellEditing();
            }
            editedValue = model.getValueAt(editedRow, editedColumn).toString();
        }
        
        return editedValue;
    }
    
    public static Object get_RecordID(javax.swing.JTable tb){
        try{
            return tb.getValueAt(tb.getSelectedRow(), 0);
        }catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    } 
    
    public static Object[] get_RecordIDs(javax.swing.JTable tb){
        Object[] recordIDs = new Object[tb.getSelectedRowCount()];
        int[] selectedRows = tb.getSelectedRows();

        for (int i = 0; i < selectedRows.length; i++) {
            try {
                recordIDs[i] = tb.getValueAt(selectedRows[i], 0);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                recordIDs[i] = 0; 
            }
        }
        return recordIDs;
    } 
    
    public static boolean has_NoZeroVal(Object[] array){
        for(Object i : array){
            if(i == "0"){
                return false;
            }
        }
        return true;
    }
    
    public static String first_LetterUpperCase(String text){
        if (text.isEmpty()) {
            return null; 
        } else {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
    }  
    
    public static boolean isValueExists(String valueToCheck, String column,String table){
        boolean exist = false;
        String query = "SELECT * FROM "+table+" WHERE "+column+" = LOWER(?)";
        try{
            Database.prepare = Database.connection.prepareStatement(query);
            Database.prepare.setString(1, valueToCheck.toLowerCase());
            
            Database.result = Database.prepare.executeQuery();
            
            if(Database.result.next()){
                exist = true;
            }
            
            Database.prepare.close();
            Database.result.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return exist;
    }
   
    public static String[] getAllValue(JTextField addProduct, JTextField addDescription, JTextField addQuantity, JComboBox addCategory, JTextField addPrice, JDateChooser addDate,Component parentComponent) {
        try {
            String getProduct = first_LetterUpperCase(addProduct.getText().trim());
            String getDescription = first_LetterUpperCase(addDescription.getText().trim());
            String getQuantity = addQuantity.getText();
            String getCategory = addCategory.getSelectedItem().toString();
            String getPrice = addPrice.getText();

            if (getProduct.isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, "Product name cannot be empty.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            } else if (isValueExists(getProduct, Database.inventoryColumns[2], Database.inventoryTable)) {
                JOptionPane.showMessageDialog(parentComponent, "Product with this name already exists.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getDescription.isEmpty()) {
                JOptionPane.showMessageDialog(parentComponent, "Description cannot be empty.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getQuantity.isEmpty() || Integer.parseInt(getQuantity) < 1) {
                JOptionPane.showMessageDialog(parentComponent, "Quantity must be greater than 0.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (getPrice.isEmpty() || Double.parseDouble(getPrice) < 1.0) {
                JOptionPane.showMessageDialog(parentComponent, "Price must be greater than 0.0.","Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new String[]{getCategory, getProduct, getDescription, getQuantity, getPrice, get_AddedDate(addDate).toString()};
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentComponent, "Invalid number format. Please enter valid numeric values.","Message", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, "Invalid input. Please check your entries.","Message", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    public static String getComboxFromTable(JTable table) {
        int row = table.getSelectedRow();
        TableCellEditor editor = table.getCellEditor(row, 1);    
        if (editor instanceof DefaultCellEditor) {
            Component editorComponent = ((DefaultCellEditor) editor).getComponent();
            if (editorComponent instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) editorComponent;
                Object selectedItem = comboBox.getSelectedItem();
                if (selectedItem != null) {
                    return selectedItem.toString();
                }
            }
        }
        return ""; 
    }
}
