/**	$Id: MsExMailMessage.java,v 1.2 2002/11/12 23:18:41 jonc Exp $
 *
 *	Intermediate MailMessage from the Microsoft Exchange
 * 	Expands attachments if possible
 *
 */
package wsl.fw.msgserver;

import java.io.FileInputStream;
import wsl.fw.msgserver.converters.WordReader;

import java.io.IOException;

public class MsExMailMessage
	extends MailMessage
{
	/*
	 *	MS Word markers
	 */
	private static String
		MSWORD_EXTENSION	= ".doc";

	/**
	 *	Constructor
	 */
	public
	MsExMailMessage (
	 String id,
	 String subject,
	 String text,
	 String type,
	 String sender,
	 String timeReceived,
	 String unread,
	 String senderEmail,
	 MsExAttachment attachments [])
	{
		super (
			id, subject, expand (text, attachments), type,
			sender, "", timeReceived, unread,
			senderEmail);
	}

	/*
	 *	Attempt to Expand attachements
	 */
	private static String
	expand (
	 String text,
	 MsExAttachment attachments [])
	{
		String result = text;

		if (attachments != null)
		{
			for (int i = 0; i < attachments.length; i++)
			{
				if (attachments [i] == null)
				{
					result += "\n" + "Internal error converting attachment";

				} else
				{
					result += "\n"
						+ "["
						+ attachments [i]._name
						+ " (" + (i + 1) + "/" + attachments.length + ")"
						+ "]\n";

					if (attachments [i]._pathname != null)
					{
						/*
						 *	Attempt conversions
						 */
						if (attachments [i]._name.endsWith (MSWORD_EXTENSION))
						{
							try
							{
								FileInputStream fIn = new FileInputStream (attachments [i]._pathname);
								WordReader doc = new WordReader (fIn);
								result += doc.getAllText ();

							} catch (IOException e)
							{
								result += "*Internal conversion error: "
									+ e.getMessage () + "*";
							}
						}
					}
				}
			}
		}
		return result;
	}
}