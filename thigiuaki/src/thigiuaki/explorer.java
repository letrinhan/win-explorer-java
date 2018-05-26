package thigiuaki;

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
import java.nio.file.Files;
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

	FileOutputStream fos;
	ZipOutputStream zipos;
	FileInputStream fis;
	ZipEntry ze;
	File file;
	ZipInputStream zis;
	JFileChooser fc;
	private JTextField tfSearch;
	List<String> filesListInDir = new ArrayList<String>();

	private File dirFrom;
	private File dirTo;
	private File back;
	private File next;
	private File previousPath;
	private String nameFile;
	private String curPath;
	private String prevPath[];
	private int i = 0;

	public Container getGui() {

		if (gui == null) {
			gui = new JPanel();
			gui.setMinimumSize(new Dimension(0, 0));
			gui.setBorder(new EmptyBorder(5, 5, 5, 5));

			fileSystemView = FileSystemView.getFileSystemView();
			desktop = Desktop.getDesktop();

			JPanel detailView = new JPanel();
			detailView.setRequestFocusEnabled(false);
			detailView.setOpaque(false);
			detailView.setMinimumSize(new Dimension(600, 220));
			detailView.setIgnoreRepaint(true);
			detailView.setAutoscrolls(true);

			table = new JTable();
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
			tableScroll.setBounds(0, 42, 746, 273);
			Dimension d = tableScroll.getPreferredSize();
			detailView.setLayout(null);

			JPanel fileMainDetails = new JPanel();
			fileMainDetails.setBounds(0, 0, 746, 37);
			detailView.add(fileMainDetails);
			fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));
			fileMainDetails.setLayout(null);

			JPanel fileDetailsLabels = new JPanel();
			fileDetailsLabels.setBounds(6, 0, 0, 0);
			fileMainDetails.add(fileDetailsLabels);
			fileDetailsLabels.setLayout(null);

			JPanel fileDetailsValues = new JPanel();
			fileDetailsValues.setBounds(0, 2, 742, 35);
			fileMainDetails.add(fileDetailsValues);
			fileDetailsValues.setLayout(null);
			path = new JTextField(29);
			path.setBounds(28, 6, 424, 22);
			fileDetailsValues.add(path);
			path.setHorizontalAlignment(SwingConstants.LEFT);

			tfSearch = new JTextField();
			tfSearch.setToolTipText("Create new file/folder");
			tfSearch.setBounds(464, 6, 164, 22);
			tfSearch.setHorizontalAlignment(SwingConstants.LEFT);
			fileDetailsValues.add(tfSearch);
			tfSearch.setColumns(13);

			JButton btnSearch = new JButton("Search");
			btnSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

				}
			});
			btnSearch.setBounds(640, 5, 102, 25);
			fileDetailsValues.add(btnSearch);

			int count = fileDetailsLabels.getComponentCount();
			tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
			detailView.add(tableScroll);

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
			// System.out.println("Root : " + roots);

			tree = new JTree(treeModel);
			tree.setMinimumSize(new Dimension(70, 16));
			tree.setMaximumSize(new Dimension(70, 16));
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			JScrollPane treeScroll = new JScrollPane(tree);
			treeScroll.setMinimumSize(new Dimension(100, 27));

			tree.setVisibleRowCount(15);

			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(200, (int) preferredSize.getHeight());
			treeScroll.setPreferredSize(widePreferred);
			for (int ii = 0; ii < count; ii++) {
				fileDetailsLabels.getComponent(ii).setEnabled(false);
			}

			JPanel fileView = new JPanel();
			fileView.setBounds(0, 315, 746, 51);
			fileView.setLayout(null);

			detailView.add(fileView);

			JToolBar toolBar = new JToolBar();
			toolBar.setBounds(0, 0, 746, 48);
			fileView.add(toolBar);
			toolBar.setFloatable(false);

			JButton btnOpen = new JButton("Open");
			btnOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File open = new File(path.getText());
					try {
						if (open.isFile()) {
							openFile(open);
						} else if (open.isDirectory()) {
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(open);
							showChildren(node);
						}

					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
			});
			toolBar.add(btnOpen);

			JButton btnCopy = new JButton("Copy");
			btnCopy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dirFrom = new File(path.getText());
					String name = path.getText();
					String[] words = name.split("\\\\");
					for (String w : words) {
						nameFile = w;
					}
				}
			});
			btnCopy.setMaximumSize(new Dimension(100, 25));
			toolBar.add(btnCopy);

			JButton btnPaste = new JButton("Paste");
			btnPaste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (dirFrom.isDirectory()) {
						dirTo = new File(path.getText() + "\\" + nameFile);
						explorer.copyFolder(dirFrom, dirTo);
					} else if (dirFrom.isFile()) {
						dirTo = new File(path.getText() + "\\" + nameFile);
						// System.out.println(dirTo);
						try {
							copyFile(dirFrom, dirTo);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});
			btnPaste.setMaximumSize(new Dimension(100, 25));
			toolBar.add(btnPaste);
			gui.repaint();
			gui.setLayout(null);

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detailView);

			JPanel panel = new JPanel();
			treeScroll.setColumnHeaderView(panel);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

			JButton btnNewButton_1 = new JButton("Back");
			btnNewButton_1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						back = new File(path.getText());
						if (back.getParent() == null) {
							back = new File(path.getText());
						} else {
							previousPath = new File(back.getParent());
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(previousPath);
							showChildren(node);
							path.setText(back.getParent());
						}
					}
				}
			});
			btnNewButton_1.setIcon(
					new ImageIcon(explorer.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
			btnNewButton_1.setHorizontalAlignment(SwingConstants.LEFT);
			panel.add(btnNewButton_1);

			JButton btnNewButton = new JButton("Next");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					// if(i>0) {
					// next = new File(prevPath[i]);
					// if(next.isDirectory()) {
					// DefaultMutableTreeNode node = new DefaultMutableTreeNode(next);
					// showChildren(node);
					// }
					// i--;
					// }
					// next = new File(prevPath[i]);
					String name = path.getText();
					next = new File(name);
					if (next.isDirectory()) {
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(next);
						showChildren(node);
					}
				}
			});
			btnNewButton.setIcon(
					new ImageIcon(explorer.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
			panel.add(btnNewButton);
			splitPane.setBounds(5, 0, 965, 373);
			gui.add(splitPane);

		}
		return gui;
	}

	public static void copyFile(File oldLocation, File newLocation) throws IOException {
		if (oldLocation.exists()) {
			BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldLocation));
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newLocation, false));
			try {
				byte[] buff = new byte[8192];
				int numChars;
				while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
					writer.write(buff, 0, numChars);
				}
			} catch (IOException ex) {
				throw new IOException(
						"IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
			} finally {
				try {
					if (reader != null) {
						writer.close();
						reader.close();
					}
				} catch (IOException ex) {
					// Log.e(TAG, "Error closing files when transferring " + oldLocation.getPath() +
					// " to " + newLocation.getPath() );
				}
			}
		} else {
			throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to "
					+ newLocation.getPath());
		}
	}

	public static void copyFolder(File source, File destination) {
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}

			String files[] = source.list();

			for (String file : files) {
				File srcFile = new File(source, file);
				File destFile = new File(destination, file);

				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = null;
			OutputStream out = null;

			try {
				in = new FileInputStream(source);
				out = new FileOutputStream(destination);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			} catch (Exception e) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public static void openFile(File path) throws IOException {
		if (!Desktop.isDesktopSupported()) {
			System.out.println("Desktop is not supported");
			return;
		}

		Desktop desktop = Desktop.getDesktop();
		if (path.exists())
			desktop.open(path);
	}

	void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (!Files.isSymbolicLink(f.toPath())) {
					deleteDir(f);
				}
			}
		}
		file.delete();
	}

	public static void deleteFile(File element) {
		if (element.isDirectory()) {
			for (File sub : element.listFiles()) {
				deleteFile(sub);
			}
		}
		element.delete();
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
				f.setMinimumSize(new Dimension(1000, 420));
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
