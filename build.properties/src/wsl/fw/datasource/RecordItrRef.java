/**	$Id: RecordItrRef.java,v 1.2 2002/07/22 23:47:06 jonc Exp $
 *
 *  RecordItrRef gets around the problem of not being able to hand
 *  RecordItr back from the Server to the Client. Its as a reference-key
 *  for the Server to map back to a RecordItr, which unfortunately must
 *  be done manually...
 *
 */
package wsl.fw.datasource;

import java.io.Serializable;

public class RecordItrRef
	implements Serializable
{
	static int _nextKey;

	private int _key;
	private String _sessionId;

	public
	RecordItrRef (
	 String sessionId)
	{
		_key = getNextKey ();
		_sessionId = sessionId;
	}

	public int
	getKey ()
	{
		return _key;
	}

	public String
	getSessionId ()
	{
		return _sessionId;
	}

	private static synchronized int
	getNextKey ()
	{
		return _nextKey++;
	}
}
