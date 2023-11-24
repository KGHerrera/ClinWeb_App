package modelo;

public class Cita {
    private int id;
    private int pacienteId;
    private int personalId;
    private int salaId;
    private String fechaHora;
    private String motivoCita;

    public Cita(int id, int pacienteId, int personalId, int salaId, String fechaHora, String motivoCita) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.personalId = personalId;
        this.salaId = salaId;
        this.fechaHora = fechaHora;
        this.motivoCita = motivoCita;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public int getPersonalId() {
        return personalId;
    }

    public void setPersonalId(int personalId) {
        this.personalId = personalId;
    }

    public int getSalaId() {
        return salaId;
    }

    public void setSalaId(int salaId) {
        this.salaId = salaId;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivoCita() {
        return motivoCita;
    }

    public void setMotivoCita(String motivoCita) {
        this.motivoCita = motivoCita;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "id_cita=" + id +
                ", fk_paciente=" + pacienteId +
                ", fk_personal=" + personalId +
                ", fk_sala=" + salaId +
                ", fecha_hora='" + fechaHora + '\'' +
                ", motivo_cita='" + motivoCita + '\'' +
                '}';
    }
}
