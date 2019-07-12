package appfood.hugobianquini.com.appfoodni.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import appfood.hugobianquini.com.appfoodni.helper.ConfiguraçãoFirebase;

public class Avaliação {

    private String idUsuario;
    private List<String> idEmpresa;;
    private String verificação;

    public Avaliação() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguraçãoFirebase.getFirebase();
        DatabaseReference avaliacaoRef = firebaseRef.child("avaliacao").child(getIdUsuario());
        avaliacaoRef.setValue(this);

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<String> getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(List<String> idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getVerificação() {
        return verificação;
    }

    public void setVerificação(String verificação) {
        this.verificação = verificação;
    }
}
