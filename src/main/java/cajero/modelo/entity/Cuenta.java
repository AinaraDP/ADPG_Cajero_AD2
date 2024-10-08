package cajero.modelo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Getter
@Setter
@Entity
@Table(name="cuentas")
public class Cuenta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_cuenta")
	private int idCuenta;
	
	private double saldo;
	
	@Column(name="tipo_cuenta")
	private String tipoCuenta;
	
	
	public void ingresar(double cantidad) {
		saldo += cantidad;
	}
	
	public boolean extraer(double cantidad) {
		if(cantidad > saldo) {
			return false;
		}
		saldo -= cantidad;
		return true;
	}
	
}
