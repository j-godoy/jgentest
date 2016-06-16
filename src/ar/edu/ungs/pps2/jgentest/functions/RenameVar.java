package ar.edu.ungs.pps2.jgentest.functions;

import java.util.StringTokenizer;

// Renombra variables usada al convertir ciclos en ifs anidados
public class RenameVar
{
	public final static String ADD_TO_VAR_IN_WHILE = "_xeditx_";

	public static boolean isRenamedInWhile(String varName)
	{
		return varName.contains(ADD_TO_VAR_IN_WHILE) && !varName.startsWith(ADD_TO_VAR_IN_WHILE)
				&& endsWithNumber(varName);
	}

	private static boolean endsWithNumber(String var)
	{
		return getLastNumber(var) != null;
	}

	/**
	 * Reemplazo _xeditx_n por _xeditx_n+1
	 * 
	 * @param varName
	 * @return
	 */
	public static String getNextVar(String varName)
	{
		if (!isRenamedInWhile(varName))
			return varName;

		return varName.substring(0, varName.lastIndexOf(ADD_TO_VAR_IN_WHILE) + ADD_TO_VAR_IN_WHILE.length())
				+ (getLastNumber(varName) + 1);
	}

	private static Integer getLastNumber(String varName)
	{
		Integer n = null;
		try
		{
			n = Integer.parseInt(
					varName.substring(varName.lastIndexOf(ADD_TO_VAR_IN_WHILE) + ADD_TO_VAR_IN_WHILE.length()));
		} catch (Exception e)
		{
			return null;
		}
		return n;
	}

	public static String renameVarsIn(String content)
	{
		StringBuilder newContentWithRenamedVars = new StringBuilder();
		StringTokenizer st = new StringTokenizer(content, " ()", true);
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (RenameVar.isRenamedInWhile(token))
				newContentWithRenamedVars.append(RenameVar.getNextVar(token));
			else
				newContentWithRenamedVars.append(token);
		}
		return newContentWithRenamedVars.toString();
	}

}
