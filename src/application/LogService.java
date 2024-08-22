package application;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.*;

import session.DBConnection;
import session.DBSync;
import session.Log;

public class LogService {
	
	private JTable _table;
	
	private void ClearRows(DefaultTableModel model) {
		int count = model.getRowCount();
		for (int i = count - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}
	
	private void ResizeRows() {
		for (int column = 0; column < _table.getColumnCount(); column++)
		{
		    TableColumn tableColumn = _table.getColumnModel().getColumn(column);
		    int preferredWidth = tableColumn.getMinWidth();
		    int maxWidth = tableColumn.getMaxWidth();
		    for (int row = 0; row < _table.getRowCount(); row++)
		    {
		        TableCellRenderer cellRenderer = _table.getCellRenderer(row, column);
		        Component c = _table.prepareRenderer(cellRenderer, row, column);
		        int width = c.getPreferredSize().width + _table.getIntercellSpacing().width;
		        preferredWidth = Math.max(preferredWidth, width);
		 
		        //  We've exceeded the maximum width, no need to check other rows
		 
		        if (preferredWidth >= maxWidth)
		        {
		            preferredWidth = maxWidth;
		            break;
		        }
		    }
		    
		    preferredWidth += 10;
		    tableColumn.setPreferredWidth( preferredWidth );
		}
	}
	
	private void AtualizarTabela() {
		DefaultTableModel m = (DefaultTableModel) _table.getModel();
		ClearRows(m);
		
		List<Log> logs = DBSync.GetInstance().Logs;
		for (int i = 0; i < logs.size(); i++) {
			Log l = logs.get(i);
			m.addRow(new Object[] { l.get_id(), l.get_app(), l.get_actionType(), l.get_user(), l.get_changes() });
		}

		ResizeRows();
	}
	
	public LogService(JTable table) {
		_table = table;
	}
	
	public void OnAppFocus() {
		AtualizarTabela();
	}
}
