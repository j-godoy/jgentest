package ar.edu.ungs.pps2.jgentest.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class StoreFile
{
	private String				path;
	private String				extension;
	private String				textoAGuardar;
	private String				nombreArchivo;
	private String				charset;
	public final static String	CHARSET_UTF8	= "utf-8";

	/**
	 *
	 * @param path:
	 *            ruta donde se va a guardar el archivo
	 * @param extension:
	 *            puede ser vacío [""]. En caso de agregarlo, agregar el punto
	 *            '.'
	 * @param textoAGuardar:
	 *            Cadena de texto que se desea guardar en disco
	 * @param nombreArchivo:
	 *            nombre con el cual se guardará el archivo (SIN extensión)
	 * @param charset:
	 *            formato charset con el que se guardará el archivo. Ej: "utf-8"
	 */
	public StoreFile(String path, String extension, String textoAGuardar, String nombreArchivo, String charset)
	{
		super();
		this.path = path;
		this.extension = extension;
		this.textoAGuardar = textoAGuardar;
		this.nombreArchivo = nombreArchivo;
		this.charset = charset;

		// Si el path contiene el nombre del archivo, sólo dejo en path hasta el
		// dir padre del archivo a crear
		if (this.path.endsWith(this.nombreArchivo))
			this.path = this.path.substring(0, (this.path.length() - this.nombreArchivo.length()));

		// Si el nombre del archivo contiene una extensión y además es la misma
		// extensión que se pasa como parámetro, entonces saco la extensión al
		// nombre del archivo
		if (this.nombreArchivo.endsWith(this.extension))
			this.nombreArchivo = this.nombreArchivo.substring(0,
					(this.nombreArchivo.length() - this.extension.length()));
	}

	public static void copyFile(String origenPath, String destinoPath) throws IOException
	{
		Path FROM = Paths.get(origenPath);
		Path TO = Paths.get(destinoPath);
		// sobreescribir el fichero de destino, si existe, y copiar
		// los atributos, incluyendo los permisos rwx
		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES };
		// Por si no existe el path completo
		new File(destinoPath).mkdirs();
		Files.copy(FROM, TO, options);
	}

	public void store() throws IOException
	{
		new File(this.path).mkdirs();
		File page = new File(this.path + this.nombreArchivo + this.extension);
		Writer out;
		if (!this.charset.trim().isEmpty())
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(page), this.charset), 4096);
		else
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(page)), 4096);
		try
		{
			out.write(this.textoAGuardar);
		} finally
		{
			out.close();
		}
	}
}
