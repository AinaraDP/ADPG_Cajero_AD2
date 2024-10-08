package cajero.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cajero.modelo.dao.CuentaDao;
import cajero.modelo.entity.Cuenta;

@RestController
public class CuentasRestController {

	@Autowired
	private CuentaDao cdao;
	
	
	@GetMapping("/todos")
	public List<Cuenta> findAll(){
		return cdao.findAll();
		
	}
}
