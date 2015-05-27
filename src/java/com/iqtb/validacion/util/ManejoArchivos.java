/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iqtb.validacion.util;

import com.iqtb.validacion.pojo.Usuarios;
import static com.iqtb.validacion.util.Bitacoras.registrarBitacora;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.apache.log4j.Logger;

/**
 *
 * @author danielromero
 */
public class ManejoArchivos {

    private File archivo;
    private FileReader archivoLector;
    private BufferedReader buferLector;
    private String linea;
    private List<Imagen> listaImagen = new ArrayList<Imagen>();
    String imagen = "<imageExpression class=\"java.lang.String\"><![CDATA[\"";
    String imagenfin = "\"]]></imageExpression>";
    private static ZipOutputStream zos;
    private static String dirParent = null;
    private static Integer numero = null;
    private int BUFFER_SIZE = 8192;
    private String descBitacora;
    private Logger logger = Logger.getLogger(ManejoArchivos.class);

    public ManejoArchivos() {
    }

    public List<Imagen> ListaImagenes(String ruta) throws IOException {
        this.archivo = new File(ruta);
        List<String> listaIma = new ArrayList<String>();

        try {
            archivoLector = new FileReader(this.archivo);
        } catch (FileNotFoundException e) {
            logger.error("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid() + " - metodo_listaImagenes ManejoArchivos ] Error al leer al archivo: " + e.getMessage());
        }
        buferLector = new BufferedReader(archivoLector);

        String nombreIma = "";
        String Carpeta = "";
        while (buferLector.ready()) {
            if (!(linea = buferLector.readLine()).equals("\000")) {
                if ((linea.indexOf(imagen) != -1) && ((linea.indexOf(imagenfin) != -1))) {
                    nombreIma = linea.substring(linea.lastIndexOf("CDATA[") + 7, linea.lastIndexOf("\"]]></imageExpression>"));
                    Imagen imagen = new Imagen();
                    Carpeta = nombreCarpeta(nombreIma);
                    imagen.setCarpeta(Carpeta);
                    imagen.setRuta(nombreIma);
                    nombreIma = nombreImagen(nombreIma);
                    imagen.setNombre(nombreIma);
                    listaImagen.add(imagen);
                }
            }
        }
        buferLector.close();
        return listaImagen;
    }

    //Metodo para borrar el contenido de un directorio
    public void borrarDirectorio(File directorio) {
        if (directorio.exists()) {
            File[] ficheros = directorio.listFiles();

            for (int x = 0; x < ficheros.length; x++) {
                if (ficheros[x].isDirectory()) {
                    borrarDirectorio(ficheros[x]);
                }
                ficheros[x].delete();
            }
        }
    }

    public boolean renombrarArchivo(String origen, String destino) {
        File f1 = new File(origen);
        File f2 = new File(destino);

        boolean correcto = f1.renameTo(f2);
        if (correcto) {
            logger.info("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid() + " - metodo_renombrarArchivo ManejoArchivos ] Se renombro correcatmente el archivo" + f2.getName());
        } else {
            logger.error("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid() + " - metodo_renombrarArchivo ManejoArchivos ] Se pudo renombrar correcatmente el archivo");
        }
        return correcto;
    }

    public String quirtarExtension(String archivo) {
        String punto = ".", Extension = "", nombre = "";
        int cont = 0;
        for (int i = 0; i < archivo.length(); i++) {
            String sub = archivo.substring(i, i + 1);
            if (punto.equals(sub)) {
                if (cont < 1) {
                    Extension = archivo.substring(0, i);
                }
                cont++;
            }
        }
        return Extension;
    }

    public static String dameExtension(String archivo) {
        String punto = ".", Extension = "", nombre = "";
        int cont = 0;
        for (int i = archivo.length(); i > 0; i--) {
            String sub = archivo.substring(i - 1, i);
            if (punto.equals(sub)) {
                if (cont < 1) {
                    Extension = archivo.substring(i - 1);
                }
                cont++;
            }
        }
        return Extension;
    }

    public String nombreImagen(String nombreImagen) {
        int pos1 = 0;
        for (int i = nombreImagen.length() - 1; i >= 0; i--) {
            String letra = nombreImagen.substring(i, i + 1);
            if (letra.equals("/")) {
                pos1 = i;
                return nombreImagen.substring(pos1 + 1);
            }
        }
        return nombreImagen;
    }

    public String nombreCarpeta(String nombreCarpeta) {
        int pos1 = 0;
        if (!nombreCarpeta.contains("/")) {
            return "";
        }
        for (int i = nombreCarpeta.length() - 1; i >= 0; i--) {
            String letra = nombreCarpeta.substring(i, i + 1);
            if (letra.equals("/")) {
                pos1 = i;
                return nombreCarpeta.substring(0, pos1);
            }
        }
        return nombreCarpeta;
    }

    //Metodo que te retorna la lista de subreportes
    public List<String> getListaSubreportes(String directorio) {
        File file = new File(directorio);
        List<String> listaSubreportes = new ArrayList<String>();
        if (file.exists()) {
            File[] ficheros = file.listFiles();
            for (int x = 0; x < ficheros.length; x++) {
                String fichero = ficheros[x].getName();
                String extencion = dameExtension(fichero);
                if (extencion.equals(".xprint")) {
                    if (!fichero.equals("main.xprint")) {
                        listaSubreportes.add(ficheros[x].getName());
                    }
                }
            }
            return listaSubreportes;
        } else {
            logger.error("No existe directorio ");
        }
        return null;

    }

    public boolean compilarPlantilla(String rutaorigen) {
        Boolean ban = false;
        String nombre = quirtarExtension(rutaorigen);

        File archivoCompilado = new File(nombre + ".jasper");
        logger.info("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid() + " - metodo_compilarPlantilla ManejoArchivos ] ruta del archivo compilado: " + archivoCompilado.getAbsolutePath());
        try {
            JasperCompileManager.compileReportToFile(rutaorigen, archivoCompilado.getAbsolutePath());
            ban = true;
            if (ban) {
                logger.info(" - metodo_compilarPlantilla ManejoArchivos ] Se compilo correctamente la plantilla: " + archivoCompilado.getName());
            } else {
                logger.error("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid() + " - metodo_compilarPlantilla ManejoArchivos ] No se pudo llevar a cabo la compilacion de plantilla: " + archivoCompilado.getName());
            }
            return ban;
        } catch (JRException ex) {
            descBitacora = "ERROR " + ex.getMessage();
            registrarBitacora(null, null, null, descBitacora, "ERROR");
            logger.error("[ " + ((Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario")).getUserid() + " - metodo_compilarPlantilla ManejoArchivos ] Error de compilacion: " + ex.getMessage());
        }
        return ban;
    }

    public static void makeZip(File file, String dirDestino, int cantidad)
            throws IOException {

        numero = cantidad;
        zos = new ZipOutputStream(new FileOutputStream(dirDestino/* + "/" + "BFC890918_BC"file.getName() + ".zip"*/));
        zos.setLevel(3);
        dirParent = file.getParent();
        recurseFiles(file);
        zos.close();
    }

    private static void recurseFiles(File file)
            throws IOException {
        if (file.isDirectory()) {
            File[] archivo = file.listFiles();
            if (archivo != null) {
                for (int i = 0; i < archivo.length; i++) {
                    recurseFiles(archivo[i]);
                }
            }
        } else {
            byte[] buf = new byte[1024];
            ZipEntry zipEntry = new ZipEntry(file.getPath().substring(dirParent.length() + numero));
            FileInputStream fin = new FileInputStream(file);

            BufferedInputStream in = new BufferedInputStream(fin);
            zos.putNextEntry(zipEntry);
            int len;
            while ((len = in.read(buf)) >= 0) {
                zos.write(buf, 0, len);
            }
            in.close();
            zos.closeEntry();
        }
    }

    public String calculaSHA1(String archivo) throws Exception {

        String filepath = archivo;
        String resultado = "";

        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        FileInputStream fileInput = new FileInputStream(filepath);
        byte[] dataBytes = new byte[1024];

        int bytesRead = 0;

        while ((bytesRead = fileInput.read(dataBytes)) != -1) {
            messageDigest.update(dataBytes, 0, bytesRead);
        }
        byte[] digestBytes = messageDigest.digest();
        StringBuffer sb = new StringBuffer("");

        for (int i = 0; i < digestBytes.length; i++) {
            sb.append(Integer.toString((digestBytes[i] & 0xff)
                    + 0x100, 16).substring(1));
        }
        logger.info(" - metodo_calculaSHA! ManejoArchivos ] " + archivo + " SumaSHA!: " + resultado);
        resultado = sb.toString();
        fileInput.close();
        return resultado;
    }

    public List<String> getSucursales(String ruta) throws IOException {
        List<String> lista = new ArrayList<String>();
        this.archivo = new File(ruta);
        try {
            archivoLector = new FileReader(archivo);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        buferLector = new BufferedReader(archivoLector);
        boolean ban = false;
        String carlos = "";
        int num = 0;
        while (buferLector.ready()) {
            if (!(linea = buferLector.readLine()).equals("\000")) {
//                System.out.println("linea: " + linea.length());
                if ((num = linea.length()) > 0) {
                    for (int x = 0; x < linea.length(); x++) {
                        if (linea.charAt(x) != ' ' && linea.charAt(x) != '"' && linea.charAt(x) != ' ') {
                            carlos += linea.charAt(x);
                            ban = true;
                        } else {
                        }
                    }
                    lista.add(carlos);
                }
                carlos = "";
            } else {
//                System.out.println("ya no hay lineas");
            }
        }
        buferLector.close();
        return lista;
    }

    public boolean eliminaArchivo(String ruta) {
        File file = new File(ruta);
        boolean ban = false;
        if (file.isFile()) {
            ban = file.delete();
        }
        return ban;
    }

    public void doUnzip(String inputZip, String destinationDirectory)
            throws IOException {
        int BUFFER = 2048;
        List zipFiles = new ArrayList();
        File sourceZipFile = new File(inputZip);
        File unzipDestinationDirectory = new File(destinationDirectory);
        unzipDestinationDirectory.mkdir();

        ZipFile zipFile;
        // Open Zip file for reading
        zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

        // Create an enumeration of the entries in the zip file
        Enumeration zipFileEntries = zipFile.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

            String currentEntry = entry.getName();

            File destFile = new File(unzipDestinationDirectory, currentEntry);
            destFile = new File(unzipDestinationDirectory, destFile.getName());

            if (currentEntry.endsWith(".zip")) {
                zipFiles.add(destFile.getAbsolutePath());
            }

            // grab file's parent directory structure
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            try {
                // extract file if not a directory
                if (!entry.isDirectory()) {
                    BufferedInputStream is
                            = new BufferedInputStream(zipFile.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest
                            = new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        zipFile.close();

        for (Iterator iter = zipFiles.iterator(); iter.hasNext();) {
            String zipName = (String) iter.next();
            doUnzip(
                    zipName,
                    destinationDirectory
                    + File.separatorChar
                    + zipName.substring(0, zipName.lastIndexOf(".zip")));
        }

    }

    public void unzip(String zipFile) throws ZipException,
            IOException {

        System.out.println(zipFile);;
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = zipFile.substring(0, zipFile.length() - 4);

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

            String currentEntry = entry.getName();

            File destFile = new File(newPath, currentEntry);
            destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();
            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip
                        .getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }
            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                unzip(destFile.getAbsolutePath());
            }
        }
    }

    public boolean eliminar(String ruta) {
        File archivo = new File(ruta);
        if (archivo.exists()) {
            if (archivo.isDirectory()) {
                logger.info("Se eliminara el Contenido de la carpeta: " + ruta);
                File[] archivos = archivo.listFiles();
                for (int i = 0; i < archivos.length; i++) {
                    File file = archivos[i];
                    if (file.isDirectory()) {
                        eliminar(file.getAbsolutePath());
                    } else {
                        if (file.delete()) {
                            logger.info("Archivo: " + file.getAbsolutePath() + " eliminado Correctamente");
                        } else {
                            logger.error("Error al Eliminar el Archivo: " + file.getAbsolutePath());
                        }
                    }
                }
                if (archivo.delete()) {
                    logger.info("Carpeta: " + ruta + " Eliminada");
                } else {
                    logger.error("Error al Eliminar la Carpeta: " + ruta);
                }
            } else {
                logger.info("Se eliminara el Archivo: " + ruta);
                if (archivo.delete()) {
                    logger.info("Archivo: " + archivo.getAbsolutePath() + " eliminado Correctamente");
                } else {
                    logger.error("Error al Eliminar el Archivo: " + archivo.getAbsolutePath());
                }
            }
        } else {
            logger.warn("La carpeta " + ruta + " no existe.");
        }
        return true;
    }

    public void mover(String origen, String destino) {
        File fileorigen = new File(origen);
        if (fileorigen.exists()) {
            logger.info("El Origen si existe");
            if (fileorigen.isFile()) {
                logger.info("El origen es un Archivo");
                File filedestino = new File(destino);
                if (!filedestino.exists()) {
                    if (fileorigen.renameTo(filedestino)) {
                        logger.info("Se movio Corectamente");
                    } else {
                        logger.error("No se pudo mover");
                    }
                } else {
                    logger.error("Imposible mover Archivo, el destino " + filedestino.getAbsolutePath() + " ya existe");
                }
            }
            if (fileorigen.isDirectory()) {
                logger.info("El origen es una Carpeta");
                File carpetadestino = new File(destino);
                if (carpetadestino.mkdirs()) {
                    logger.info("Carpetas Destino Creadas");
                } else {
                    logger.error("Error al crear Carpetas destino");
                }
                if (!origen.endsWith("/")) {
                    origen = origen + "/";
                }
                if (!destino.endsWith("/")) {
                    destino = destino + "/";
                }
                File[] subs = fileorigen.listFiles();
                for (int i = 0; i < subs.length; i++) {
                    File file = subs[i];
                    if (file.isFile()) {
                        logger.info("Archivo a mover: " + file.getAbsolutePath());

                        mover(file.getAbsolutePath(), destino + file.getName());
                    }
                    if (file.isDirectory()) {
                        logger.info("Carpeta a mover: " + file.getName());
                        mover(origen + file.getName(), destino + file.getName());
                    }
                }
                if (fileorigen.delete()) {
                    logger.info("Carpeta: " + fileorigen.getAbsolutePath() + " eliminada");
                } else {
                    logger.error("Error al eliminar Carpeta: " + fileorigen.getAbsolutePath());
                }
            }
        } else {
            logger.warn("El origen " + origen + " no existe");
        }
    }

    public boolean extractZipTo(String zipFilename, String destinationPath) {
        logger.info("Iniciando Extraccion del ZIP de: " + zipFilename + " a: " + destinationPath);
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(zipFilename);
            CheckedInputStream checksum = new CheckedInputStream(fis, new Adler32());

            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));

            File directory = new File(destinationPath);

            if (!((directory.exists()) && (directory.canWrite()))) {
                logger.info("Creando directorio destino " + directory.getAbsolutePath());
                File dir = new File(destinationPath);
                dir.mkdir();
                //FileUtil.createDirectory(destinationPath);
            }
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File f = new File(destinationPath + System.getProperty("file.separator") + entry.getName());
                logger.debug("f " + f.getAbsolutePath());
                if (entry.isDirectory()) {
                    logger.info("Se encontro una carpeta, no se creara, Pero se extraeran los archivos que contiene");
                } else {
                    String nom = f.getName().toLowerCase();
                    if ((nom.endsWith(".xml") || nom.endsWith(".pdf")) && !entry.getName().trim().startsWith("__MACOSX")) {
                        byte[] data = new byte[2048];

                        FileOutputStream fos = new FileOutputStream(destinationPath + System.getProperty("file.separator") + f.getName());
                        logger.debug("Archivo " + destinationPath + System.getProperty("file.separator") + f.getName());
                        dest = new BufferedOutputStream(fos, 2048);
                        int count;
                        while ((count = zis.read(data, 0, 2048)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                    } else {
                        logger.warn("Archivo " + nom + " extension invalida.");
                    }
                }
            }
            zis.close();
        } catch (Exception e) {
            logger.error("Error en extractZipTo: " + e);
            return false;
        }
        return true;
    }

    public boolean extractZipSocio(String zipFilename, String destinationPath, String rfcSocio) {
        List<String> archivosXml = new ArrayList<String>();
        logger.info("Iniciando Extraccion del ZIP de: " + zipFilename + " a: " + destinationPath);
        try {
            BufferedOutputStream dest = null;
            FileInputStream fis = new FileInputStream(zipFilename);
            CheckedInputStream checksum = new CheckedInputStream(fis, new Adler32());
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));

            File directory = new File(destinationPath);

            if (!((directory.exists()) && (directory.canWrite()))) {
                logger.info("Creando directorio destino " + directory.getAbsolutePath());
                File dir = new File(destinationPath);
                dir.mkdir();
            }
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File f = new File(destinationPath + System.getProperty("file.separator") + entry.getName());
                logger.debug("f " + f.getAbsolutePath());
                if (entry.isDirectory()) {
                    logger.info("Se encontro una carpeta, no se creara, Pero se extraeran los archivos que contiene");
                } else {
                    String nom = f.getName().toLowerCase();
                    if ((nom.endsWith(".xml") || nom.endsWith(".pdf")) && !entry.getName().trim().startsWith("__MACOSX")) {
                        byte[] data = new byte[2048];
                        FileOutputStream fos = null;
                        if (nom.endsWith(".xml")) {
                            fos = new FileOutputStream(destinationPath + System.getProperty("file.separator") + f.getName() + ".tmp");
                            logger.info("Agregado para revisar RFC " + destinationPath + System.getProperty("file.separator") + f.getName() + ".tmp");
                            archivosXml.add(destinationPath + System.getProperty("file.separator") + f.getName() + ".tmp");
                        } else {
                            fos = new FileOutputStream(destinationPath + System.getProperty("file.separator") + f.getName());
                        }
                        logger.debug("Archivo " + destinationPath + System.getProperty("file.separator") + f.getName());
                        dest = new BufferedOutputStream(fos, 2048);
                        int count;
                        while ((count = zis.read(data, 0, 2048)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                    } else {
                        logger.warn("Archivo " + nom + " extension invalida.");
                    }
                }
            }
            zis.close();
            revisarTmp(archivosXml, rfcSocio);
        } catch (IOException e) {
            logger.error("Error en extractZipTo: " + e);
            return false;
        }
        return true;
    }

    public void revisarTmp(List<String> listaArchivos, String rfcSocio) {
        FileInputStream fis = null;
        InputStream in = null;
        FileOutputStream fos = null;
        BufferedOutputStream dest = null;
        RevisarXML revisarXml;
        String rfc = null;
        String dirXml = null;
        try {
            for (String item : listaArchivos) {
                logger.info("Nombre archivo " + item);

                fis = new FileInputStream(item);
                revisarXml = new RevisarXML(fis);
                rfc = revisarXml.revisaNodos("rfc", "cfdi:Comprobante", "Emisor");
                logger.info("termina revision");
                logger.info("rfc encontrado " + rfc);
                logger.info("rfc socio " + rfcSocio);
                if (rfcSocio.equals(rfc)) {
                    logger.info("Emisor rfc: " + rfc + " coincide con el rfc del socio comercial " + rfcSocio);
                    fis = new FileInputStream(item);
                    byte[] data = new byte[2048];
                    dirXml = item.replace(".tmp", "");
                    logger.info("Archivo a escribir" + dirXml);
                    fos = new FileOutputStream(dirXml);
                    dest = new BufferedOutputStream(fos, 2048);
                    int count;
                    while ((count = fis.read(data, 0, 2048)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } else {
                    logger.info("Emisor rfc: " + rfc + " no coincide con el rfc del socio comercial " + rfcSocio);
                }
                fis.close();
                logger.info("Eliminando " + item);
                eliminar(item);
            }
        } catch (FileNotFoundException e) {
            logger.error("revisarTmp FileNotFoundException ERROR " + e.getMessage());
        } catch (IOException ex) {
            logger.error("revisarTmp IOException ERROR" + ex.getMessage());
        }

    }

}
