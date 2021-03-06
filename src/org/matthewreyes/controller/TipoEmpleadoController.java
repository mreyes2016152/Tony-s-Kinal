
package org.matthewreyes.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.matthewreyes.beam.TipoEmpleado;
import org.matthewreyes.db.Conexion;
import org.matthewreyes.system.Principal;

public class TipoEmpleadoController implements Initializable{
	 
	 private Principal escenarioPrincipal;
	 private enum operaciones{NUEVO, GUARDAR, CANCELAR, ELIMINAR, EDITAR, ACTUALIZAR, NINGUNO};
	 private operaciones tipoDeOperacion = operaciones.NINGUNO;
	 private ObservableList<TipoEmpleado> listaTipoEmpleado;
	 @FXML private TextField txtCodigoTipoEmpleado;
	 @FXML private TextField txtDescripcion;
	 @FXML private TableView tblTipoEmpleados;
	 @FXML private TableColumn colCodigoTipoEmpleado;
	 @FXML private TableColumn colDescripcion;
	 @FXML private Button btnNuevo;
	 @FXML private Button btnEliminar;
	 @FXML private Button btnEditar;
	 @FXML private Button btnReporte;
	 
	 @Override
	 public void initialize(URL location, ResourceBundle resources) {
		  cargarDatos();
	 }
	 
	 public void cargarDatos(){
		  tblTipoEmpleados.setItems(getTipoEmpleado());
		  colCodigoTipoEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, Integer>("codigoTipoEmpleado"));
		  colDescripcion.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, String>("descripcion"));
	 }
	 
	 public ObservableList<TipoEmpleado> getTipoEmpleado(){
		  ArrayList<TipoEmpleado> lista = new ArrayList<TipoEmpleado>();
		  try{
			   PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarTipoEmpleado()}");
			   ResultSet resultado = procedimiento.executeQuery();
			   while(resultado.next()){
					lista.add(new TipoEmpleado(resultado.getInt("codigoTipoEmpleado"),
										resultado.getString("descripcion")
					));
			   } 
		  }catch(Exception e){
			   e.printStackTrace();
		  }
		  return listaTipoEmpleado = FXCollections.observableArrayList(lista);
	 }
	 
	 public void seleccionarElemento(){
		  txtCodigoTipoEmpleado.setText(String.valueOf(((TipoEmpleado)tblTipoEmpleados.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado()));
		  txtDescripcion.setText(String.valueOf(((TipoEmpleado)tblTipoEmpleados.getSelectionModel().getSelectedItem()).getDescripcion()));
	 }
	 
	 public void desactivarBarra(){
		  txtCodigoTipoEmpleado.setEditable(false);
		  txtDescripcion.setEditable(false);
	 }
	 
	 public void activarBarra(){
		  txtCodigoTipoEmpleado.setEditable(false);
		  txtDescripcion.setEditable(true);
	 }
	 
	 public void limpiarBarra(){
		  txtCodigoTipoEmpleado.setText("");
		  txtDescripcion.setText("");
	 }
	 
	 public void ButtonNuevo(){
		  switch (tipoDeOperacion){
			   case NINGUNO:
					activarBarra();
					limpiarBarra();
					btnNuevo.setText("Guardar");
					btnEliminar.setText("Cancelar");
					btnEliminar.setDisable(false);
					btnEditar.setDisable(true);
					btnReporte.setDisable(true);		
					tipoDeOperacion = operaciones.GUARDAR;
			   break;
			   case GUARDAR:
					guardarDatos();
					desactivarBarra();
					limpiarBarra();
					btnNuevo.setText("Nuevo");
					btnEliminar.setText("Eliminar");
					btnEditar.setDisable(false);
					btnReporte.setDisable(false);
					tipoDeOperacion = operaciones.NINGUNO;
					cargarDatos();					
			   break;						 
		  }  
	 }
	 
	 public void guardarDatos(){
		  TipoEmpleado registro = new TipoEmpleado();
		  registro.setDescripcion(txtDescripcion.getText());
		  try{
			   PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarTipoEmpleado(?)}");
			   procedimiento.setString(1, registro.getDescripcion());
			   procedimiento.executeQuery();
			   listaTipoEmpleado.add(registro);
		  }catch(Exception e){
			   e.printStackTrace();
		  }
	 }
	 
	 public void buttonEliminar(){
			   switch (tipoDeOperacion){
					case GUARDAR:
						 desactivarBarra();
						 limpiarBarra();
						 btnNuevo.setText("Nuevo");
						 btnEliminar.setText("Eliminar");
						 btnEditar.setDisable(false);
						 btnReporte.setDisable(false);
						 tipoDeOperacion = operaciones.NINGUNO;
						 cargarDatos();							 					 
					break;
					default:
						 if(tblTipoEmpleados.getSelectionModel().getSelectedItem() != null){
							  int respuesta = JOptionPane.showConfirmDialog(null,"??Estas seguro de eliminar el registro?","Eliminar empresa",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
							  if(respuesta == JOptionPane.YES_OPTION){
								   try{
										PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarTipoEmpleado(?)}");
										procedimiento.setInt(1, ((TipoEmpleado)tblTipoEmpleados.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado());
										procedimiento.executeQuery();
										limpiarBarra();
										listaTipoEmpleado.remove(tblTipoEmpleados.getSelectionModel().getSelectedIndex());
								   }catch(Exception e){
										e.printStackTrace();
								   }
							  }
						 }
						 else
							  JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento antes de eliminar");
					break;					
			   }
	 }
	 
	 public void buttonActualizar(){
		  switch(tipoDeOperacion){
			   case NINGUNO:
					if (tblTipoEmpleados.getSelectionModel().getSelectedItem() != null){
						 btnEliminar.setDisable(true);
						 btnNuevo.setDisable(true);
						 btnEditar.setText("Actualizar");
						 btnReporte.setText("Cancelar");
						 activarBarra();
						 tipoDeOperacion = operaciones.EDITAR;
					}else
						 JOptionPane.showMessageDialog(null, "Selecciona un elemento antes de editar");
			   break;
			   case EDITAR:
					actualizarDatos();
					limpiarBarra();
					desactivarBarra();
					btnEliminar.setDisable(false);
					btnNuevo.setDisable(false);
					btnEditar.setText("Editar");
					btnReporte.setText("Reporte");
					cargarDatos();
					tipoDeOperacion = operaciones.NINGUNO;
			   break;
		  }		  
	 }
	 
	 public void buttonReporte(){
		  switch(tipoDeOperacion){
			   case EDITAR:
					limpiarBarra();
					desactivarBarra();
					btnEditar.setText("Editar");
					btnReporte.setText("Reporte");
					btnNuevo.setDisable(false);
					btnEliminar.setDisable(false);
					cargarDatos();
					tipoDeOperacion = operaciones.NINGUNO;
			   break;
		  }
	 }
	 
	 public void actualizarDatos(){
		  try{
			   PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarTipoEmpleado(?,?)}");
			   TipoEmpleado registro = (TipoEmpleado)tblTipoEmpleados.getSelectionModel().getSelectedItem();
			   registro.setDescripcion(txtDescripcion.getText());
			   procedimiento.setInt(1, registro.getCodigoTipoEmpleado());
			   procedimiento.setString(2, registro.getDescripcion());
			   procedimiento.executeQuery();
		  }catch(Exception e){
			   e.printStackTrace();
		  }
	 }	 
	 
	 public Principal getEscenarioPrincipal(){
		  return escenarioPrincipal;
	 } 
	 
	 public void setEscenarioPrincipal(Principal escenarioPrincipal){
		  this.escenarioPrincipal = escenarioPrincipal;
	 }
	 
	 public void MenuPrincipal(){
		  escenarioPrincipal.MenuPrincipal();
	 }	 
	 
	 public void VentanaEmpleados(){
		  escenarioPrincipal.VentanaEmpleados();
	 }
}
