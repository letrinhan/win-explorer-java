package fileex;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;
import java.awt.FlowLayout;

public class explorer {

	public static final String APP_TITLE = "FileBro";
	private Desktop desktop;
	private FileSystemView fileSystemView;
	@SuppressWarnings("unused")
	private File currentFile;
	private JPanel gui;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JTable table;
	private FileTableModel fileTableModel;
	private ListSelectionListener listSelectionListener;
	private boolean cellSizesSet = false;
	private int rowIconPadding = 6;
	private JTextField path;
	@SuppressWarnings("unused")
	private JPanel newFilePanel;
	@SuppressWarnings("unused")
	private JRadioButton newTypeFile;
	@SuppressWarnings("unused")
	private JTextField name;

	
	private JTextField tfSearch;

	public Container getGui() {

		if (gui == null) {
			gui = new JPanel();
			gui.setBorder(new EmptyBorder(5, 5, 5, 5));

			fileSystemView = FileSystemView.getFileSystemView();
			desktop = Desktop.getDesktop();

			JPanel detailView = new JPanel(new BorderLayout(3, 3));

			table = new JTable();
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setAutoCreateRowSorter(true);
			table.setShowVerticalLines(false);

			listSelectionListener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent lse) {
					int row = table.getSelectionModel().getLeadSelectionIndex();
					setFileDetails(((FileTableModel) table.getModel()).getFile(row));
				}
			};
			table.getSelectionModel().addListSelectionListener(listSelectionListener);
			JScrollPane tableScroll = new JScrollPane(table);
			Dimension d = tableScroll.getPreferredSize();

			JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
			detailView.add(fileMainDetails, BorderLayout.NORTH);
			fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));

			JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
			fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

			JPanel fileDetailsValues = new JPanel();
			fileMainDetails.add(fileDetailsValues, BorderLayout.SOUTH);
			fileDetailsValues.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			path = new JTextField(29);
			fileDetailsValues.add(path);
			path.setHorizontalAlignment(SwingConstants.LEFT);

			tfSearch = new JTextField();
			tfSearch.setHorizontalAlignment(SwingConstants.LEFT);
			fileDetailsValues.add(tfSearch);
			tfSearch.setColumns(13);

			JButton btnSearch = new JButton("Search");
			fileDetailsValues.add(btnSearch);

			int count = fileDetailsLabels.getComponentCount();
			tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
			detailView.add(tableScroll, BorderLayout.CENTER);

			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);

			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tse) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
					showChildren(node);
					setFileDetails((File) node.getUserObject());
				}
			};

			File[] roots = fileSystemView.getRoots();
			for (File fileSystemRoot : roots) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
				root.add(node);
				File[] files = fileSystemView.getFiles(fileSystemRoot, true);
				for (File file : files) {
					if (file.isDirectory()) {
						node.add(new DefaultMutableTreeNode(file));
					}
				}
			}

			tree = new JTree(treeModel);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			JScrollPane treeScroll = new JScrollPane(tree);

			tree.setVisibleRowCount(15);

			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(200, (int) preferredSize.getHeight());
			treeScroll.setPreferredSize(widePreferred);

			JToolBar toolBar = new JToolBar();
			toolBar.setFloatable(false);
			for (int ii = 0; ii < count; ii++) {
				fileDetailsLabels.getComponent(ii).setEnabled(false);
			}

			JPanel fileView = new JPanel(new BorderLayout(3, 3));

			fileView.add(toolBar, BorderLayout.EAST);
			
			JButton btnPaste_1 = new JButton("Paste");
			btnPaste_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnPaste(currentFile, currentFile);
					JOptionPane.showMessageDialog(null,"Complete");
				}

				private void btnPaste(File currentFile, File currentFile2) {
					// TODO Auto-generated method stub
					
				}
			});
			toolBar.add(btnPaste_1);
			
			JButton btnPaste = new JButton("Paste");
			btnPaste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnPaste(currentFile, currentFile);
					JOptionPane.showMessageDialog(null,"Complete");

            	}

				private void btnPaste(File currentFile, File currentFile2) {
					// TODO Auto-generated method stub
					
				}
				
			});
			

			detailView.add(fileView, BorderLayout.SOUTH);
			
			JButton btnCopy = new JButton("Copy");
			btnCopy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					copyFile(currentFile, currentFile);
					JOptionPane.showMessageDialog(null,"Complete");

				}

				private void copyFile(File currentFile, File currentFile2) {
					// TODO Auto-generated method stub
					
				}
			});
			fileView.add(btnCopy, BorderLayout.WEST);
			gui.setLayout(null);

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detailView);

			JPanel panel = new JPanel();
			treeScroll.setColumnHeaderView(panel);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

			JButton btnNewButton_1 = new JButton("Back");
			btnNewButton_1.setIcon(
					new ImageIcon(explorer.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
				}
			});
			btnNewButton_1.setHorizontalAlignment(SwingConstants.LEFT);
			panel.add(btnNewButton_1);

			JButton btnNewButton = new JButton("Next");
			btnNewButton.setIcon(
					new ImageIcon(explorer.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
			panel.add(btnNewButton);
			splitPane.setBounds(5, 5, 659, 268);
			gui.add(splitPane);

		}
		return gui;
	}

	public void showRootFile() {
		tree.setSelectionInterval(0, 0);
	}

	@SuppressWarnings("unused")
	private TreePath findTreePath(File find) {
		for (int ii = 0; ii < tree.getRowCount(); ii++) {
			TreePath treePath = tree.getPathForRow(ii);
			Object object = treePath.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			File nodeFile = (File) node.getUserObject();

			if (nodeFile == find) {
				return treePath;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(gui, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

	@SuppressWarnings("unused")
	private void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(gui, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
		gui.repaint();
	}

	private void setTableData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (fileTableModel == null) {
					fileTableModel = new FileTableModel();
					table.setModel(fileTableModel);
				}
				table.getSelectionModel().removeListSelectionListener(listSelectionListener);
				fileTableModel.setFiles(files);
				table.getSelectionModel().addListSelectionListener(listSelectionListener);
				if (!cellSizesSet) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					table.setRowHeight(icon.getIconHeight() + rowIconPadding);

					setColumnWidth(0, -1);
					setColumnWidth(3, 60);
					table.getColumnModel().getColumn(3).setMaxWidth(120);

					cellSizesSet = true;
				}
			}
		});
	}

	private void setColumnWidth(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		if (width < 0) {
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			Dimension preferred = label.getPreferredSize();
			width = (int) preferred.getWidth() + 14;
		}
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}

	private void showChildren(final DefaultMutableTreeNode node) {
		tree.setEnabled(false);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true); // !!
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);
							}
						}
					}
					setTableData(files);
				}
				return null;
			}

			@Override
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			protected void done() {
				tree.setEnabled(true);
			}
		};
		worker.execute();
	}

	@SuppressWarnings("unused")
	private void setFileDetails(File file) {
		currentFile = file;
		Icon icon = fileSystemView.getSystemIcon(file);
		path.setText(file.getPath());

		JFrame f = (JFrame) gui.getTopLevelAncestor();
		if (f != null) {
			f.setTitle(APP_TITLE + " :: " + fileSystemView.getSystemDisplayName(file));
		}

		gui.repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}
				JFrame f = new JFrame(APP_TITLE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				explorer FileBrowser = new explorer();
				f.setContentPane(FileBrowser.getGui());

				try {
					URL urlBig = FileBrowser.getClass().getResource("fb-icon-32x32.png");
					URL urlSmall = FileBrowser.getClass().getResource("fb-icon-16x16.png");
					ArrayList<Image> images = new ArrayList<Image>();
					images.add(ImageIO.read(urlBig));
					images.add(ImageIO.read(urlSmall));
					f.setIconImages(images);
				} catch (Exception weTried) {
				}

				f.pack();
				f.setMinimumSize(new Dimension(690, 320));
				f.setVisible(true);

				FileBrowser.showRootFile();
			}
		});
	}
	@SuppressWarnings("serial")
	class FileTableModel extends AbstractTableModel {

		private File[] files;
		private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		private String[] columns = { "Icon", "File", "Path/name", "Size",

		};

		FileTableModel() {
			this(new File[0]);
		}

		FileTableModel(File[] files) {
			this.files = files;
		}

		public Object getValueAt(int row, int column) {
			File file = files[row];
			switch (column) {
			case 0:
				return fileSystemView.getSystemIcon(file);
			case 1:
				return fileSystemView.getSystemDisplayName(file);
			case 2:
				return file.getPath();
			case 3:
				return file.length();
			default:
				System.err.println("Logic Error");
			}
			return "";
		}

		public int getColumnCount() {
			return columns.length;
		}

		public Class<?> getColumnClass(int column) {
			switch (column) {
			case 0:
				return ImageIcon.class;
			case 3:
				return Long.class;

			}
			return String.class;
		}

		public String getColumnName(int column) {
			return columns[column];
		}

		public int getRowCount() {
			return files.length;
		}

		public File getFile(int row) {
			return files[row];
		}

		public void setFiles(File[] files) {
			this.files = files;
			fireTableDataChanged();
		}
	}

	@SuppressWarnings("serial")
	class FileTreeCellRenderer extends DefaultTreeCellRenderer {

		private FileSystemView fileSystemView;

		private JLabel label;

		FileTreeCellRenderer() {
			label = new JLabel();
			label.setOpaque(true);
			fileSystemView = FileSystemView.getFileSystemView();
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			File file = (File) node.getUserObject();
			label.setIcon(fileSystemView.getSystemIcon(file));
			label.setText(fileSystemView.getSystemDisplayName(file));
			label.setToolTipText(file.getPath());

			if (selected) {
				label.setBackground(backgroundSelectionColor);
				label.setForeground(textSelectionColor);
			} else {
				label.setBackground(backgroundNonSelectionColor);
				label.setForeground(textNonSelectionColor);
			}

			return label;
		}
	}
}