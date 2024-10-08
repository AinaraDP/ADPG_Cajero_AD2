package cajero.modelo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cajero.modelo.entity.Cuenta;
import cajero.modelo.repository.CuentaRepository;

@Repository
public class CuentaDaoImplMy8Jpa implements CuentaDao {

	@Autowired
	private CuentaRepository crepo;

	@Override
	public Cuenta findById(int idCuenta) {

		return crepo.findById(idCuenta).orElse(null);
	}
	
	@Override
	public List<Cuenta> findAll() {
		
		return crepo.findAll();
	}


	@Override
	public Cuenta login(int idCuenta) {
		
		return crepo.cuentaPorNumeroCuenta(idCuenta);
	}

	@Override
	public Cuenta modificar(Cuenta cuenta) {
		try {
			return crepo.save(cuenta);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

}
