package e.gusta.gerenciador_local.models;

import java.util.Date;

public class Local implements Comparable<Local> {
    private String idLocal;
    private String idUsuario;
    private Date dataCadastro;
    private String descricao;
    private double latitude;
    private double longitude;
    private String idImagem;


    public String getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(String idLocal) {
        this.idLocal = idLocal;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getdataCadastro() {
        return dataCadastro;
    }

    public void setdataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getLat() {
        return latitude;
    }

    public void setLat(double latitude) {
        this.latitude = latitude;
    }

    public double getLong() {
        return longitude;
    }

    public void setLong(double longitude) {
        this.longitude = longitude;
    }

    public String getIdImagem() {
        return idImagem;
    }

    public void setIdImagem(String idImagem) {
        this.idImagem = idImagem;
    }

    public Local(String idLocal, String idUsuario, Date dataCadastro, String descricao, double latitude, double longitude, String idImagem) {
        this.idLocal = idLocal;
        this.idUsuario = idUsuario;
        this.dataCadastro = dataCadastro;
        this.descricao = descricao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.idImagem = idImagem;
    }

    public Local() {
    }

    @Override
    public int compareTo(Local local) {
        return this.dataCadastro.compareTo(local.dataCadastro);
    }

    @Override
    public String toString() {
        return "Local{" +
                "idLocal='" + idLocal + '\'' +
                "idUsuario='" + idUsuario + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", descricao='" + descricao + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", idImagem='" + idImagem + '\'' +
                '}';
    }
}
