package cajero.modelo.dao;

import java.util.List;

import cajero.modelo.entity.Cuenta;
import cajero.modelo.entity.Movimiento;

public interface MovimientoDao {

	Movimiento findById(int idMovimiento);
	List <Movimiento> findAll();
	Movimiento insertarMovimiento(Movimiento movimiento);
	List<Movimiento> findByCuenta(int idCuenta);
	
	
}
