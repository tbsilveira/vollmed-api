package med.voll.api.domain.medico;

import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.consulta.MotivoCancelamento;
import med.voll.api.domain.endereco.DadosEndereco;
import med.voll.api.domain.paciente.DadosCadastroPaciente;
import med.voll.api.domain.paciente.Paciente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MedicoRepositoryTest {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Deveria devolver null quando único médico cadastrado não está disponível na data")
    void escolherMedicoAleatorioLivreNaDataCenario1() {

        // given or arrange
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10,0);

        var medico = cadastrarMedico("Médico", "medico@email.com", "123123", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Paciente", "paciente@email.com", "00011122233");
        cadastrarConsulta(medico, paciente, proximaSegundaAs10, null);

        // when or act
        var medicoLivre = medicoRepository.escolherMedicoAleatorioLivreNaData(Especialidade.CARDIOLOGIA, proximaSegundaAs10);

        // then or assert
        assertThat(medicoLivre).isNull();  //import Assertions.assertThat from [org.assertj.core.api]
    }


    @Test
    @DisplayName("Deveria devolver o médico quando houver disponibilidade na data")
    void escolherMedicoAleatorioLivreNaDataCenario2() {
        // given or arrange
        var proximaSegundaAs10 = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .atTime(10,0);

        var medico = cadastrarMedico("Médico", "medico@email.com", "123123", Especialidade.CARDIOLOGIA);

        // when or act
        var medicoLivre = medicoRepository.escolherMedicoAleatorioLivreNaData(Especialidade.CARDIOLOGIA, proximaSegundaAs10);

        // then or assert
        assertThat(medicoLivre).isEqualTo(medico);  //import Assertions.assertThat from [org.assertj.core.api]
    }



    private DadosCadastroMedico dadosMedico(String nome, String email, String crm, Especialidade especialidade) {
        return new DadosCadastroMedico(nome, email, "000111222", crm, especialidade, dadosEndereco());
    }

    private DadosCadastroPaciente dadosPaciente(String nome, String email, String cpf) {
        return new DadosCadastroPaciente(nome, email, "000111222", cpf, dadosEndereco());
    }

    private DadosEndereco dadosEndereco() {
        return new DadosEndereco("Rua XPTO", "bairro", "00055533", "New York", "NY", null, null);
    }

    private void cadastrarConsulta(Medico medico, Paciente paciente, LocalDateTime data, MotivoCancelamento motivoCancelamento) {
        em.persist(new Consulta(null, medico, paciente, data, null));
    }

    private Medico cadastrarMedico(String nome, String email, String crm, Especialidade especialidade) {
        var medico = new Medico(dadosMedico(nome, email, crm, especialidade));
        em.persist(medico);
        return medico;
    }

    private Paciente cadastrarPaciente(String nome, String email, String cpf) {
        var paciente = new Paciente(dadosPaciente(nome, email, cpf));
        em.persist(paciente);
        return paciente;
    }

}