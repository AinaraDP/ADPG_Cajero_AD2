package cajero.modelo.dao;

import java.util.List;

import cajero.modelo.entity.Cuenta;

public interface CuentaDao {

	Cuenta findById(int idCuenta);
	Cuenta login(int idCuenta);
	Cuenta modificar(Cuenta cuenta);
	List<Cuenta> findAll();
}
