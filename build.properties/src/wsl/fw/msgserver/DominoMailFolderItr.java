/**	$Id: DominoMailFolderItr.java,v 1.8 2004/10/29 00:12:08 tecris Exp $
 *
 * 	Converts documents in the folder to MailMessages
 *
 */
package wsl.fw.msgserver;

import lotus.domino.*;
import java.io.*;

import wsl.fw.resource.ResId;
import wsl.fw.html.WslHtmlParser;
import wsl.fw.msgserver.converters.WordReader;

public class DominoMailFolderItr
	extends DominoFolderItr
{


	private static final ResId
		LABEL_ATT_SHOWN		= new ResId ("ImapFolder.label.AttachDisplayed"),
		LABEL_ATT_HIDDEN	= new ResId ("ImapFolder.label.AttachUnshown"),
		LABEL_NO_SUBJECT	= new ResId ("ImapFolder.label.NoSubject");


	public static final String
		MAIL_SUBJECT		= "Subject",
		MAIL_BODY			= "Body",
		MAIL_SENDER			= "From",
		MAIL_RECIPIENT		= "To",
		MAIL_RECEIVED		= "DeliveredDate",
		TEXT_EXTENSION		= "txt",
		MSWORD_EXTENSION	= "doc",
		HTML_EXTENSION		= "html",
		EMPTY_STRING		= "";

	private final String _folderId;

	public
	DominoMailFolderItr (
	 Database db,
	 View v)
		throws NotesException
	{
		super (db, v);
		_folderId = v.getName ();
	}

	public ItemDobj
	docToItemDobj (
	 Document doc)
		throws NotesException
	{
		String subject = getDocValue (doc,MAIL_SUBJECT);
		if (subject==null || subject.trim ().equals (EMPTY_STRING))
			subject = LABEL_NO_SUBJECT.getText ();
		MailMessage mail = new MailMessage (
								doc.getUniversalID (),
								subject,
								getMessageText (doc),
								"",			// type
								getDocValue (doc, MAIL_SENDER),
								getDocValue (doc, MAIL_RECIPIENT),
								getDocValue (doc, MAIL_RECEIVED),
								"false",	// unread
								getDocValue (doc, MAIL_SENDER));
									 //sender mail
		return new MailMessageDobj (_folderId, mail);
	}

	/**
	 * creates the message text
	 * appends to the message body the text from any
	 * MS Word document and plain text attachments
	 */
	private String
	getMessageText (
	 Document dc)
		throws NotesException
	{		
		
		String text = "";
		java.util.Vector v = dc.getItems ();
		int n = 1; 	// counter for the attachments
		/**
		 * find the number of the attachments for this message
		 * attachments are Items of type Item.ATTACHMENTS
		 */
		for (int i=0;i<v.size ();i++)
		{
			Item item = (Item)v.elementAt (i);
			if (item.getType ()==Item.ATTACHMENT)
			{
				n++;
			}
		}
		// add the message body
		text += "["
			 + LABEL_ATT_SHOWN.getText ()
			 + " " + "1/" + n
			 + "]\n"
			 + getDocValue (dc, MAIL_BODY);
		int c = 2;
		if (n!=1)
		{
			for (int i=0;i<v.size ();i++)
			{
				Item item = (Item)v.elementAt (i); 			
				if (item.getType ()==Item.ATTACHMENT)
				{
					String fileName = item.getValueString ();
					EmbeddedObject attch = (EmbeddedObject)
						dc.getAttachment (item.getValueString ());
					BufferedInputStream is = new BufferedInputStream (
													attch.getInputStream ());
					String extension = fileName.substring(fileName.indexOf('.')+1);
					String attchText = "";
					String label = LABEL_ATT_SHOWN.getText ();
					
					System.out.println("extensie="+extension);
					// MS Word files
					if (extension.equalsIgnoreCase (MSWORD_EXTENSION))
					{
						try
						{
							WordReader doc = new WordReader (is);
							attchText = doc.getAllText ()==null?
												"Can't read document":
												doc.getAllText ();
						} catch (IOException e)
						{
							e.printStackTrace ();
						}
						//  plain text files
					} else if (extension.equalsIgnoreCase (TEXT_EXTENSION))
					{
						int k;
						try
						{
							while ((k=is.read ())!=-1)
								attchText +=
									new Character ((char)k).toString ();
						} catch (IOException e)
						{
							e.printStackTrace ();
						}
					//  plain text files
					} else if (extension.equalsIgnoreCase (HTML_EXTENSION))
					{
						int k;
						WslHtmlParser parser = new WslHtmlParser ();
						try
						{
							while ((k=is.read ())!=-1)
								attchText +=
									new Character ((char)k).toString ();
							attchText = parser.parseHtml(attchText);
						} catch (IOException e)
						{
							e.printStackTrace ();
						}
					} else
					{
						label = LABEL_ATT_HIDDEN.getText ();
						attchText = "";
					}
					text += "["
						 + label
						 + " " + c + "/" + n
						 + "]\n"
						 + attchText + "\n";
					c++;
				}
			}
		}
		return text;
	}
}
