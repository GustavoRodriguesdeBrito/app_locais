package e.gusta.gerenciador_local.models;

import java.util.Date;

public class Local implements Comparable<Local>{
    private String idUsuario;
    private Date dataCadsatro;
    private String descricao;
    private double latitude;
    private double longitude;
    private String idImagem;


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getDataCadsatro() {
        return dataCadsatro;
    }

    public void setDataCadsatro(Date dataCadsatro) {
        this.dataCadsatro = dataCadsatro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIdImagem() {
        return idImagem;
    }

    public void setIdImagem(String idImagem) {
        this.idImagem = idImagem;
    }

    public Local (){}
    @Override
    public int compareTo(Local local) {
        return this.dataCadsatro.compareTo(local.dataCadsatro);
    }
}
