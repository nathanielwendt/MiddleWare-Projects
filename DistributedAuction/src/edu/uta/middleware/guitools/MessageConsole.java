package edu.uta.middleware.guitools;

import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/*
 *  Create a simple console to display text messages.
 *
 *  Messages can be directed here from different sources. Each source can
 *  have its messages displayed in a different color.
 *
 *  Messages can either be appended to the console or inserted as the first
 *  line of the console
 *
 *  You can limit the number of lines to hold in the Document.
 */
public class MessageConsole extends JFrame {

	private JPanel contentPane;
	private JTextComponent textComponent;
	private Document document;
	private boolean isAppend;
	private DocumentListener limitLinesListener;
	private JScrollPane consoleScrollPane;
	private JTextPane consoleTextPane;


	private MessageConsole(String windowName)
	{
		this(windowName,true);
	}

	/*
	 *	Use the text component specified as a simply console to display
	 *  text messages.
	 *
	 *  The messages can either be appended to the end of the console or
	 *  inserted as the first line of the console.
	 */
	private MessageConsole(String windowName, boolean isAppend){
		super(windowName);
		this.textComponent = getConsoleTextPane();
		this.document = textComponent.getDocument();
		this.isAppend = isAppend;
		textComponent.setEditable( false );
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		getConsoleScrollPane().setBounds(6, 6, 438, 266);
		contentPane.add(getConsoleScrollPane());
	}

	/*
	 *  Redirect the output from the standard output to the console
	 *  using the default text color and null PrintStream
	 */
	private void redirectOut(){
		redirectOut(null, null);
	}

	/**
	 * Creates a debug/message console by redirecting the 
	 * Systemout and Systemerr for that particular JVM to the 
	 * console so that it is easy for the user to visualize what exactly
	 * is happening.
	 * @param windowName - the name of the window.
	 */
	public static void invokeDebugConsole(final String windowName){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageConsole frame = new MessageConsole(windowName);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Returns the text pane that will 
	 * hold the messages
	 */
	private JTextPane getConsoleTextPane() {
		if (consoleTextPane == null) {
			consoleTextPane = new JTextPane();
		}
		return consoleTextPane;
	}

	/**
	 * Returns the scrollpane that will contain
	 * the text pane that contains the messages.
	 * This scrollpane can be added to a JFrame
	 * to start off a message console.
	 */
	public JScrollPane getConsoleScrollPane() {
		if (consoleScrollPane == null) {
			consoleScrollPane = new JScrollPane();
			consoleScrollPane.setViewportView(getConsoleTextPane());
			consoleScrollPane.setPreferredSize(new Dimension(450,275));
			this.redirectOut();
			this.redirectErr(Color.RED, null);
		}
		return consoleScrollPane;
	}

	/*
	 *  Redirect the output from the standard output to the console
	 *  using the specified color and PrintStream. When a PrintStream
	 *  is specified the message will be added to the Document before
	 *  it is also written to the PrintStream.
	 */
	private void redirectOut(Color textColor, PrintStream printStream){
		ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
		System.setOut( new PrintStream(cos, true) );
	}

	/*
	 *  Redirect the output from the standard error to the console
	 *  using the default text color and null PrintStream
	 */
	private void redirectErr(){
		redirectErr(null, null);
	}

	/*
	 *  Redirect the output from the standard error to the console
	 *  using the specified color and PrintStream. When a PrintStream
	 *  is specified the message will be added to the Document before
	 *  it is also written to the PrintStream.
	 */
	private void redirectErr(Color textColor, PrintStream printStream){
		ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
		System.setErr( new PrintStream(cos, true) );
	}

	/*
	 *  To prevent memory from being used up you can control the number of
	 *  lines to display in the console
	 *
	 *  This number can be dynamically changed, but the console will only
	 *  be updated the next time the Document is updated.
	 */
	private void setMessageLines(int lines){
		if (limitLinesListener != null)
			document.removeDocumentListener( limitLinesListener );
		// implement a line restriction function, incase we want to restrict the number of messages.
		document.addDocumentListener( limitLinesListener );
	}

	/*
	 *	Class to intercept output from a PrintStream and add it to a Document.
	 *  The output can optionally be redirected to a different PrintStream.
	 *  The text displayed in the Document can be color coded to indicate
	 *  the output source.
	 */
	class ConsoleOutputStream extends ByteArrayOutputStream{
		private final String EOL = System.getProperty("line.separator");
		private SimpleAttributeSet attributes;
		private PrintStream printStream;
		private StringBuffer buffer = new StringBuffer(80);
		private boolean isFirstLine;

		/*
		 *  Specify the option text color and PrintStream
		 */
		public ConsoleOutputStream(Color textColor, PrintStream printStream){
			if (textColor != null){
				attributes = new SimpleAttributeSet();
				StyleConstants.setForeground(attributes, textColor);
			}

			this.printStream = printStream;

			if (isAppend)
				isFirstLine = true;
		}

		/*
		 *  Override this method to intercept the output text. Each line of text
		 *  output will actually involve invoking this method twice:
		 *
		 *  a) for the actual text message
		 *  b) for the newLine string
		 *
		 *  The message will be treated differently depending on whether the line
		 *  will be appended or inserted into the Document
		 */
		public void flush(){
			String message = toString();

			if (message.length() == 0) return;

			if (isAppend)
				handleAppend(message);
			else
				handleInsert(message);

			reset();
		}

		/*
		 *	We don't want to have blank lines in the Document. The first line
		 *  added will simply be the message. For additional lines it will be:
		 *
		 *  newLine + message
		 */
		private void handleAppend(String message){
			if (EOL.equals(message)){
				buffer.append(message);
			}
			else{
				buffer.append(message);
				clearBuffer();
			}

		}
		/*
		 *  We don't want to merge the new message with the existing message
		 *  so the line will be inserted as:
		 *
		 *  message + newLine
		 */
		private void handleInsert(String message){
			buffer.append(message);

			if (EOL.equals(message)){
				clearBuffer();
			}
		}

		/*
		 *  The message and the newLine have been added to the buffer in the
		 *  appropriate order so we can now update the Document and send the
		 *  text to the optional PrintStream.
		 */
		private void clearBuffer(){
			//  In case both the standard out and standard err are being redirected
			//  we need to insert a newline character for the first line only

			if (isFirstLine && document.getLength() != 0){
				buffer.insert(0, "\n");
			}

			isFirstLine = false;
			String line = buffer.toString();

			try{
				if (isAppend){
					int offset = document.getLength();
					document.insertString(offset, line, attributes);
					textComponent.setCaretPosition( document.getLength() );
				}
				else{
					document.insertString(0, line, attributes);
					textComponent.setCaretPosition( 0 );
				}
			}
			catch (BadLocationException ble) {}

			if (printStream != null){
				printStream.print(line);
			}

			buffer.setLength(0);
		}
	}
}
