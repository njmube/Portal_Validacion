package com.iqtb.validacion.pojo;
// Generated 12-mar-2015 19:22:00 by Hibernate Tools 4.3.1



/**
 * RolesHasOpcionesId generated by hbm2java
 */
public class RolesHasOpcionesId  implements java.io.Serializable {


     private int idRol;
     private int idOpcion;

    public RolesHasOpcionesId() {
    }

    public RolesHasOpcionesId(int idRol, int idOpcion) {
       this.idRol = idRol;
       this.idOpcion = idOpcion;
    }
   
    public int getIdRol() {
        return this.idRol;
    }
    
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
    public int getIdOpcion() {
        return this.idOpcion;
    }
    
    public void setIdOpcion(int idOpcion) {
        this.idOpcion = idOpcion;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof RolesHasOpcionesId) ) return false;
		 RolesHasOpcionesId castOther = ( RolesHasOpcionesId ) other; 
         
		 return (this.getIdRol()==castOther.getIdRol())
 && (this.getIdOpcion()==castOther.getIdOpcion());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getIdRol();
         result = 37 * result + this.getIdOpcion();
         return result;
   }   


}


