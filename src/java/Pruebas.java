
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author danielromero
 */
public class Pruebas {

    public static void main(String[] args) {

        String numeros = "WARN: Este es un warn\n"
                + "WARN: Este es otro warn\n"
                + "WARN: Este en un nuevo warn\n"
                + "WARN: Otro mas\n"
                + "WARN: Otro con texto mas largo que los demas\n"
                + "WARN: lalalalalallalallalalallalalallaaalalallalalallalalallalalallalalalalalallalaalall lalalalalalla lalala\n"
                + "WARN: asdkksjdvnaksjnaksnckajscnasklcnakcnkajcnskjcnsakjcnsakjcnakj\n"
                + "WARN kdkjandksnkdansdkjasndkjadlaksmalskdmaslkdmaslkdmaslkdmaslkdmasldkasmlkasmld\n"
                + "WARN: lllknjkllplojnkjasiuek oasasndkjasdnaksjd oasdnlaksdalskmd laksmdlkasmklasmdlkas";
        String[] numerosComoArray = numeros.split("\n");
        for (int i = 0; i < numerosComoArray.length; i++) {
            System.out.println(numerosComoArray[i]);
        }

//        Pattern pat = Pattern.compile("(^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z-]+(\\.[a-zA-Z]+)*(\\.[a-z]{2,3}))*");
//        Matcher mat = pat.matcher("Carlos.Diaz@IDG-Corp.com");
//        if (mat.matches()) {
//            System.out.println("SI");
//        } else {
//            System.out.println("NO");
//        }
    }

    /* @PostConstruct
     public void init() {
     cfdiDataList = new LazyDataModel<ImprimirCFDI>() {

     @Override
     public List<ImprimirCFDI> load(int first, int pageSize, String sortField,
     SortOrder sortOrder, Map<String, String> filters) {
     session = HibernateUtil.getSessionFactory().openSession();
     tx = session.beginTransaction();
     try {
     lista = new ArrayList<ImprimirCFDI>();
     Criteria cr = session.createCriteria(CfdisRecibidos.class);
     Criteria crCount = session.createCriteria(CfdisRecibidos.class);
     crCount.setProjection(Projections.rowCount());
     cr.add(Restrictions.eq("idEmpresa", empresa.getIdEmpresa()));
     cr.add(Restrictions.ne("estado", "ELIMINADO"));
     cr.add(Restrictions.ne("estado", "CANCELADO"));
     crCount.add(Restrictions.eq("idEmpresa", empresa.getIdEmpresa()));
     crCount.add(Restrictions.ne("estado", "ELIMINADO"));
     crCount.add(Restrictions.ne("estado", "CANCELADO"));
     if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
     cr.add(Restrictions.eq("idSocioComercial", usuario.getIdSocioComercial()));
     crCount.add(Restrictions.eq("idSocioComercial", usuario.getIdSocioComercial()));
     }

     if (sortField != null && !sortField.isEmpty()) {
     if (sortOrder.equals(SortOrder.ASCENDING)) {
     cr.addOrder(Order.asc(sortField));
     } else {
     cr.addOrder(Order.desc(sortField));
     }
     } else {
     cr.addOrder(Order.desc("fechaRecepcion"));
     }
     if (!filters.isEmpty()) {
     Iterator it = filters.keySet().iterator();
     while (it.hasNext()) {
     String filterProperty = (String) it.next();
     String filterValue = filters.get(filterProperty);
     if (filterProperty.equals("rfcSocioComercial")) {
     Disjunction or = Restrictions.disjunction();
     rfcByIdSocio = null;
     try {
     rfcByIdSocio = daoSociosComerciales.filtroSocioComercialByRFC(empresa.getIdEmpresa(), filterValue);
     } catch (Exception ex) {
     logger.error("Error obteniendo lista de socios comerciales ERROR " + ex);
     }
     if (rfcByIdSocio != null && !rfcByIdSocio.isEmpty()) {
     for (Integer item : rfcByIdSocio) {
     or.add(Restrictions.eq("idSocioComercial", item));
     }
     } else {
     or.add(Restrictions.eq("idSocioComercial", 0));
     }
     cr.add(or);
     crCount.add(or);
     }
     if (filterProperty.equals("folio")) {
     int i = Integer.valueOf(filterValue);
     cr.add(Restrictions.eq(filterProperty, i));
     crCount.add(Restrictions.eq(filterProperty, i));
     }
     if (filterProperty.equals("serie") || filterProperty.equals("estado")) {
     cr.add(Restrictions.eq(filterProperty, filterValue));
     crCount.add(Restrictions.eq(filterProperty, filterValue));
     }
     if (filterProperty.equals("fecha") || filterProperty.equals("fechaRecepcion")) {
     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     String re1 = "^(19|20)\\d\\d([-])?$";
     Pattern p = Pattern.compile(re1, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
     Matcher m = p.matcher(filterValue);
     if (m.find()) {
     filterValue = filterValue.replace("-", "");
     fromDate = df.parse(filterValue + "-01-01 00:00:00");
     toDate = df.parse(filterValue + "-12-31 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     String re2 = "^(19|20)\\d\\d[-](0([1-9])?|1([012])?)([-])?$";
     Pattern p1 = Pattern.compile(re2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
     Matcher m1 = p1.matcher(filterValue);
     if (m1.find()) {
     if (filterValue.endsWith("-")) {
     filterValue = filterValue.substring(0, 7);
     }
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1-01 00:00:00");
     toDate = df.parse(filterValue + "9-30 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     if (filterValue.endsWith("-1")) {
     fromDate = df.parse(filterValue + "0-01 00:00:00");
     toDate = df.parse(filterValue + "2-31 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     fromDate = df.parse(filterValue + "-01 00:00:00");
     if (filterValue.endsWith("02")) {
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
     anio = Integer.parseInt(dateFormat.format(fromDate));
     if ((anio % 4 == 0) && ((anio % 100 != 0) || (anio % 400 == 0))) {
     toDate = df.parse(filterValue + "-29 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     toDate = df.parse(filterValue + "-28 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     } else {
     if (filterValue.endsWith("04") || filterValue.endsWith("06") || filterValue.endsWith("09") || filterValue.endsWith("11")) {
     toDate = df.parse(filterValue + "-30 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     toDate = df.parse(filterValue + "-31 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     }
     }
     }
     } else {
     String re3 = "^(19|20)\\d\\d([-])(0[1-9]|1[012])([-])(0[1-9]|[12][0-9]|3[01])$";
     Pattern p2 = Pattern.compile(re3, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
     Matcher m2 = p2.matcher(filterValue);
     if (m2.find()) {
     anio = Integer.parseInt(filterValue.substring(0, 4));
     mes = Integer.parseInt(filterValue.substring(5, 7));
     dia = Integer.parseInt(filterValue.substring(8, 10));
     if (dia == 31 && (mes == 4 || mes == 6 || mes == 9 || mes == 11)) {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     } else {
     if (dia >= 30 && mes == 2) {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     } else {
     if (mes == 2 && dia == 29 && !(anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0))) {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     } else {
     fromDate = df.parse(filterValue + " 00:00:00");
     toDate = df.parse(filterValue + " 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     }
     }
     } else {
     String re4 = "^(19|20)\\d\\d([-])(0[1-9]|1[012])([-])[0123]$";
     Pattern p3 = Pattern.compile(re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
     Matcher m3 = p3.matcher(filterValue);
     if (m3.find()) {
     anio = Integer.parseInt(filterValue.substring(0, 4));
     mes = Integer.parseInt(filterValue.substring(5, 7));
     if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     if (filterValue.endsWith("-3")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "0 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     } else {
     if (mes == 2) {
     if ((anio % 4 == 0) && ((anio % 100 != 0) || (anio % 400 == 0))) {
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     }
     }
     } else {
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "8 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     }
     }
     }
     } else {
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     if (filterValue.endsWith("-3")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "1 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     }
     }
     } else {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     }
     }
     }
     }
     }
     }
     }
     num = ((Long) crCount.uniqueResult()).intValue();
     this.setRowCount(num);
     cr.setFirstResult(first);
     cr.setMaxResults(pageSize + first);
     listaCFDIs = cr.list();
     if (listaCFDIs == null || listaCFDIs.size() <= 0) {
     logger.info("listaCfdisCriteria - No existen CFDIs recibidos para mostrar");
     return lista;
     }
     for (CfdisRecibidos item : listaCFDIs) {
     imCfdi = new ImprimirCFDI();
     imCfdi.setIdCfdiRecibido(item.getIdCfdiRecibido());
     imCfdi.setIdSocioComercial(item.getIdSocioComercial());
     imCfdi.setSerie(item.getSerie());
     imCfdi.setFolio(item.getFolio());
     imCfdi.setFecha(item.getFecha());
     imCfdi.setFechaRecepcion(item.getFechaRecepcion());
     imCfdi.setEstado(item.getEstado());
     imCfdi.setError(item.getError());
     try {
     scRFC = daoSociosComerciales.getRFCSociobyIdSocio(item.getIdSocioComercial());
     } catch (Exception e) {
     logger.error("listaCfdisCriteria ERROR: " + e);
     }
     imCfdi.setRfcSocioComercial(scRFC);
     lista.add(imCfdi);
     }
     } catch (ParseException e) {
     logger.error("Formato de fecha incompleto yyyy-MM-dd ERROR " + e);
     } catch (StringIndexOutOfBoundsException e) {
     logger.error("StringIndexOutOfBoundsException ERROR " + e);
     } catch (NumberFormatException e) {
     logger.warn("NumberFormatException ERROR " + e.getMessage());
     } finally {
     fromDate = null;
     toDate = null;
     listaCFDIs = null;
     if (session.isOpen()) {
     session.clear();
     session.close();
     }
     }
     return lista;
     }
     };

     lazyCfdisEliminados = null;
     lazyCfdisEliminados = new LazyDataModel<ImprimirCFDI>() {

     @Override
     public List<ImprimirCFDI> load(int first, int pageSize, String sortField,
     SortOrder sortOrder, Map<String, String> filters) {
     session = HibernateUtil.getSessionFactory().openSession();
     tx = session.beginTransaction();
     try {
     listaE = null;
     listaE = new ArrayList<ImprimirCFDI>();
     Criteria cr = session.createCriteria(CfdisRecibidos.class);
     Criteria crCount = session.createCriteria(CfdisRecibidos.class);
     crCount.setProjection(Projections.rowCount());
     cr.add(Restrictions.eq("idEmpresa", empresa.getIdEmpresa()));
     cr.add(Restrictions.or(
     Restrictions.eq("estado", "ELIMINADO"),
     Restrictions.eq("estado", "CANCELADO")));
     crCount.add(Restrictions.eq("idEmpresa", empresa.getIdEmpresa()));
     crCount.add(Restrictions.or(
     Restrictions.eq("estado", "ELIMINADO"),
     Restrictions.eq("estado", "CANCELADO")));
     if (rolUsuario.getTipo().equals(RolTipo.SOCIO_COMERCIAL.name())) {
     cr.add(Restrictions.eq("idSocioComercial", usuario.getIdSocioComercial()));
     crCount.add(Restrictions.eq("idSocioComercial", usuario.getIdSocioComercial()));
     }

     if (sortField != null && !sortField.isEmpty()) {
     if (sortOrder.equals(SortOrder.ASCENDING)) {
     cr.addOrder(Order.asc(sortField));
     } else {
     cr.addOrder(Order.desc(sortField));
     }
     } else {
     cr.addOrder(Order.desc("fechaRecepcion"));
     }
     if (!filters.isEmpty()) {
     Iterator it = filters.keySet().iterator();
     while (it.hasNext()) {
     String filterProperty = (String) it.next();
     String filterValue = filters.get(filterProperty);
     if (filterProperty.equals("rfcSocioComercial")) {
     Disjunction or = Restrictions.disjunction();
     rfcByIdSocio = null;
     try {
     rfcByIdSocio = daoSociosComerciales.filtroSocioComercialByRFC(empresa.getIdEmpresa(), filterValue);
     } catch (Exception ex) {
     logger.error("Error obteniendo lista de socios comerciales");
     }
     if (rfcByIdSocio != null && !rfcByIdSocio.isEmpty()) {
     for (Integer item : rfcByIdSocio) {
     or.add(Restrictions.eq("idSocioComercial", item));
     }
     } else {
     or.add(Restrictions.eq("idSocioComercial", 0));
     }
     cr.add(or);
     crCount.add(or);
     }
     if (filterProperty.equals("folio")) {
     int i = Integer.valueOf(filterValue);
     cr.add(Restrictions.eq(filterProperty, i));
     crCount.add(Restrictions.eq(filterProperty, i));
     }
     if (filterProperty.equals("serie") || filterProperty.equals("estado")) {
     cr.add(Restrictions.eq(filterProperty, filterValue));
     crCount.add(Restrictions.eq(filterProperty, filterValue));
     }
     if (filterProperty.equals("fecha") || filterProperty.equals("fechaRecepcion")) {
     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     String re1 = "^(19|20)\\d\\d([-])?$";
     Pattern p = Pattern.compile(re1, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
     Matcher m = p.matcher(filterValue);
     if (m.find()) {
     filterValue = filterValue.replace("-", "");
     fromDate = df.parse(filterValue + "-01-01 00:00:00");
     toDate = df.parse(filterValue + "-12-31 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     String re2 = "^(19|20)\\d\\d[-](0([1-9])?|1([012])?)([-])?$";
     Pattern p1 = Pattern.compile(re2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
     Matcher m1 = p1.matcher(filterValue);
     if (m1.find()) {
     if (filterValue.endsWith("-")) {
     filterValue = filterValue.substring(0, 7);
     }
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1-01 00:00:00");
     toDate = df.parse(filterValue + "9-30 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     if (filterValue.endsWith("-1")) {
     fromDate = df.parse(filterValue + "0-01 00:00:00");
     toDate = df.parse(filterValue + "2-31 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     fromDate = df.parse(filterValue + "-01 00:00:00");
     if (filterValue.endsWith("02")) {
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
     anio = Integer.parseInt(dateFormat.format(fromDate));
     if ((anio % 4 == 0) && ((anio % 100 != 0) || (anio % 400 == 0))) {
     toDate = df.parse(filterValue + "-29 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     toDate = df.parse(filterValue + "-28 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     } else {
     if (filterValue.endsWith("04") || filterValue.endsWith("06") || filterValue.endsWith("09") || filterValue.endsWith("11")) {
     toDate = df.parse(filterValue + "-30 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     toDate = df.parse(filterValue + "-31 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     }
     }
     }
     } else {
     String re3 = "^(19|20)\\d\\d([-])(0[1-9]|1[012])([-])(0[1-9]|[12][0-9]|3[01])$";
     Pattern p2 = Pattern.compile(re3, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
     Matcher m2 = p2.matcher(filterValue);
     if (m2.find()) {
     anio = Integer.parseInt(filterValue.substring(0, 4));
     mes = Integer.parseInt(filterValue.substring(5, 7));
     dia = Integer.parseInt(filterValue.substring(8, 10));
     if (dia == 31 && (mes == 4 || mes == 6 || mes == 9 || mes == 11)) {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     } else {
     if (dia >= 30 && mes == 2) {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     } else {
     if (mes == 2 && dia == 29 && !(anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0))) {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     } else {
     fromDate = df.parse(filterValue + " 00:00:00");
     toDate = df.parse(filterValue + " 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     }
     }
     } else {
     String re4 = "^(19|20)\\d\\d([-])(0[1-9]|1[012])([-])[0123]$";
     Pattern p3 = Pattern.compile(re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
     Matcher m3 = p3.matcher(filterValue);
     if (m3.find()) {
     anio = Integer.parseInt(filterValue.substring(0, 4));
     mes = Integer.parseInt(filterValue.substring(5, 7));
     if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     if (filterValue.endsWith("-3")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "0 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     } else {
     if (mes == 2) {
     if ((anio % 4 == 0) && ((anio % 100 != 0) || (anio % 400 == 0))) {
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     }
     }
     } else {
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "8 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     } else {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     }
     }
     }
     } else {
     if (filterValue.endsWith("-0")) {
     fromDate = df.parse(filterValue + "1 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "9 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     if (filterValue.endsWith("-3")) {
     fromDate = df.parse(filterValue + "0 00:00:00");
     toDate = df.parse(filterValue + "1 23:59:59");
     cr.add(Restrictions.between(filterProperty, fromDate, toDate));
     crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
     }
     }
     }
     } else {
     toDate = df.parse("1800-01-31 23:59:59");
     logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
     cr.add(Restrictions.lt(filterProperty, toDate));
     crCount.add(Restrictions.lt(filterProperty, toDate));
     }
     }
     }
     }
     }
     }
     }
     num1 = ((Long) crCount.uniqueResult()).intValue();
     this.setRowCount(num1);
     cr.setFirstResult(first);
     cr.setMaxResults(pageSize + first);
     listaCFDIsE = cr.list();
     if (listaCFDIsE == null || listaCFDIsE.size() <= 0) {
     logger.info("listaCfdisCriteriaE - No existen CFDIs recibidos para mostrar");
     return listaE;
     }
     for (CfdisRecibidos item : listaCFDIsE) {
     imCfdi = new ImprimirCFDI();
     imCfdi.setIdCfdiRecibido(item.getIdCfdiRecibido());
     imCfdi.setIdSocioComercial(item.getIdSocioComercial());
     imCfdi.setSerie(item.getSerie());
     imCfdi.setFolio(item.getFolio());
     imCfdi.setFecha(item.getFecha());
     imCfdi.setFechaRecepcion(item.getFechaRecepcion());
     imCfdi.setEstado(item.getEstado());
     imCfdi.setError(item.getError());
     try {
     scRFC = daoSociosComerciales.getRFCSociobyIdSocio(item.getIdSocioComercial());
     } catch (Exception e) {
     logger.error("listaCfdisCriteriaE ERROR: " + e);
     }
     imCfdi.setRfcSocioComercial(scRFC);
     listaE.add(imCfdi);
     }
     } catch (ParseException e) {
     logger.warn("Formato de fecha incompleto yyyy-MM-dd ");
     } catch (StringIndexOutOfBoundsException e) {
     logger.warn("StringIndexOutOfBoundsException ERROR " + e);
     } finally {
     fromDate = null;
     toDate = null;
     listaCFDIsE = null;
     if (session.isOpen()) {
     session.clear();
     session.close();
     }
     }
     return listaE;
     }
     };
     }*/
}

/* CONTABILIDAD ELECTRONICA
 try {
 Criteria cr = session.createCriteria(ContabilidadElectronica.class);
 Criteria crCount = session.createCriteria(ContabilidadElectronica.class);
 crCount.setProjection(Projections.rowCount());
 cr.add(Restrictions.eq("idEmpresa", empresa.getIdEmpresa()));
 crCount.add(Restrictions.eq("idEmpresa", empresa.getIdEmpresa()));

 if (sortField != null && !sortField.isEmpty()) {
 if (sortOrder.equals(SortOrder.ASCENDING)) {
 cr.addOrder(Order.asc(sortField));
 } else {
 cr.addOrder(Order.desc(sortField));
 }
 } else {
 cr.addOrder(Order.desc("fechaRecepcion"));
 }
 if (!filters.isEmpty()) {
 Iterator it = filters.keySet().iterator();
 while (it.hasNext()) {
 String filterProperty = (String) it.next();
 String filterValue = filters.get(filterProperty);
 if (filterProperty.equals("anio")) {
 int i = Integer.valueOf(filterValue);
 cr.add(Restrictions.eq(filterProperty, i));
 crCount.add(Restrictions.eq(filterProperty, i));
 }
 if (filterProperty.equals("nombreRecibido") || filterProperty.equals("nombreGenerado") || filterProperty.equals("tipoArchivo") || filterProperty.equals("estado") || filterProperty.equals("mes")) {
 cr.add(Restrictions.like(filterProperty, filterValue, MatchMode.START));
 crCount.add(Restrictions.like(filterProperty, filterValue, MatchMode.START));
 }
 if (filterProperty.equals("fechaRecepcion")) {
 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

 String re1 = "^(19|20)\\d\\d([-])?$";
 Pattern p = Pattern.compile(re1, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
 Matcher m = p.matcher(filterValue);
 if (m.find()) {
 filterValue = filterValue.replace("-", "");
 fromDate = df.parse(filterValue + "-01-01 00:00:00");
 toDate = df.parse(filterValue + "-12-31 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 String re2 = "^(19|20)\\d\\d[-](0([1-9])?|1([012])?)([-])?$";
 Pattern p1 = Pattern.compile(re2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
 Matcher m1 = p1.matcher(filterValue);
 if (m1.find()) {
 if (filterValue.endsWith("-")) {
 filterValue = filterValue.substring(0, 7);
 }
 if (filterValue.endsWith("-0")) {
 fromDate = df.parse(filterValue + "1-01 00:00:00");
 toDate = df.parse(filterValue + "9-30 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 if (filterValue.endsWith("-1")) {
 fromDate = df.parse(filterValue + "0-01 00:00:00");
 toDate = df.parse(filterValue + "2-31 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 fromDate = df.parse(filterValue + "-01 00:00:00");
 if (filterValue.endsWith("02")) {
 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
 anio = Integer.parseInt(dateFormat.format(fromDate));
 if ((anio % 4 == 0) && ((anio % 100 != 0) || (anio % 400 == 0))) {
 toDate = df.parse(filterValue + "-29 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 toDate = df.parse(filterValue + "-28 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 } else {
 if (filterValue.endsWith("04") || filterValue.endsWith("06") || filterValue.endsWith("09") || filterValue.endsWith("11")) {
 toDate = df.parse(filterValue + "-30 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 toDate = df.parse(filterValue + "-31 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 }
 }
 }
 } else {
 String re3 = "^(19|20)\\d\\d([-])(0[1-9]|1[012])([-])(0[1-9]|[12][0-9]|3[01])$";
 Pattern p2 = Pattern.compile(re3, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
 Matcher m2 = p2.matcher(filterValue);
 if (m2.find()) {
 anio = Integer.parseInt(filterValue.substring(0, 4));
 mes = Integer.parseInt(filterValue.substring(5, 7));
 dia = Integer.parseInt(filterValue.substring(8, 10));
 if (dia == 31 && (mes == 4 || mes == 6 || mes == 9 || mes == 11)) {
 toDate = df.parse("1800-01-31 23:59:59");
 logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
 cr.add(Restrictions.lt(filterProperty, toDate));
 crCount.add(Restrictions.lt(filterProperty, toDate));
 } else {
 if (dia >= 30 && mes == 2) {
 toDate = df.parse("1800-01-31 23:59:59");
 logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
 cr.add(Restrictions.lt(filterProperty, toDate));
 crCount.add(Restrictions.lt(filterProperty, toDate));
 } else {
 if (mes == 2 && dia == 29 && !(anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0))) {
 toDate = df.parse("1800-01-31 23:59:59");
 logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
 cr.add(Restrictions.lt(filterProperty, toDate));
 crCount.add(Restrictions.lt(filterProperty, toDate));
 } else {
 fromDate = df.parse(filterValue + " 00:00:00");
 toDate = df.parse(filterValue + " 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 }
 }
 } else {
 String re4 = "^(19|20)\\d\\d([-])(0[1-9]|1[012])([-])[0123]$";
 Pattern p3 = Pattern.compile(re4, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
 Matcher m3 = p3.matcher(filterValue);
 if (m3.find()) {
 anio = Integer.parseInt(filterValue.substring(0, 4));
 mes = Integer.parseInt(filterValue.substring(5, 7));
 if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
 if (filterValue.endsWith("-0")) {
 fromDate = df.parse(filterValue + "1 00:00:00");
 toDate = df.parse(filterValue + "9 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
 fromDate = df.parse(filterValue + "0 00:00:00");
 toDate = df.parse(filterValue + "9 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 if (filterValue.endsWith("-3")) {
 fromDate = df.parse(filterValue + "0 00:00:00");
 toDate = df.parse(filterValue + "0 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 } else {
 if (mes == 2) {
 if ((anio % 4 == 0) && ((anio % 100 != 0) || (anio % 400 == 0))) {
 if (filterValue.endsWith("-0")) {
 fromDate = df.parse(filterValue + "1 00:00:00");
 toDate = df.parse(filterValue + "9 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
 fromDate = df.parse(filterValue + "0 00:00:00");
 toDate = df.parse(filterValue + "9 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 toDate = df.parse("1800-01-31 23:59:59");
 logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
 cr.add(Restrictions.lt(filterProperty, toDate));
 crCount.add(Restrictions.lt(filterProperty, toDate));
 }
 }
 } else {
 if (filterValue.endsWith("-0")) {
 fromDate = df.parse(filterValue + "1 00:00:00");
 toDate = df.parse(filterValue + "9 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
 fromDate = df.parse(filterValue + "0 00:00:00");
 toDate = df.parse(filterValue + "8 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 } else {
 toDate = df.parse("1800-01-31 23:59:59");
 logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
 cr.add(Restrictions.lt(filterProperty, toDate));
 crCount.add(Restrictions.lt(filterProperty, toDate));
 }
 }
 }
 } else {
 if (filterValue.endsWith("-0")) {
 fromDate = df.parse(filterValue + "1 00:00:00");
 toDate = df.parse(filterValue + "9 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 if (filterValue.endsWith("-1") || filterValue.endsWith("-2")) {
 fromDate = df.parse(filterValue + "0 00:00:00");
 toDate = df.parse(filterValue + "9 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 if (filterValue.endsWith("-3")) {
 fromDate = df.parse(filterValue + "0 00:00:00");
 toDate = df.parse(filterValue + "1 23:59:59");
 cr.add(Restrictions.between(filterProperty, fromDate, toDate));
 crCount.add(Restrictions.between(filterProperty, fromDate, toDate));
 }
 }
 }
 } else {
 toDate = df.parse("1800-01-31 23:59:59");
 logger.info("fecha " + filterValue + " no existe se genera toDate " + toDate.toString());
 cr.add(Restrictions.lt(filterProperty, toDate));
 crCount.add(Restrictions.lt(filterProperty, toDate));
 }
 }
 }
 }
 }
 }
 }
 num = ((Long) crCount.uniqueResult()).intValue();
 this.setRowCount(num);
 cr.setFirstResult(first);
 cr.setMaxResults(pageSize + first);
 listaContabilidad = cr.list();
 } catch (NumberFormatException e) {
 logger.error("NumberFormatException ERROR " + e);
 } catch (ParseException e) {
 logger.error("ParseException ERROR " + e);
 } catch (HibernateException e) {
 logger.error("HibernateException ERROR " + e);
 } finally {
 if (session.isOpen()) {
 session.clear();
 session.close();
 }
 }S
 */
