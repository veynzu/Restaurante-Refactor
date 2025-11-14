import { Routes } from "@angular/router";
import { Dashboard } from "../dashboard/dashboard";
import { PanelControl } from "./panel-control";
import { GestionUsuarios } from "../gestion.usuarios/gestion.usuarios";
import { GestionMesas } from "../gestion.mesas/gestion.mesas";
import { PanelCocina } from "../panel.cocina/panel.cocina";
// import { Reportes } from "../reportes/reportes"; // Componente no implementado aún

export const dashboardRoutes: Routes = [
  {
    path: '',
    component: Dashboard,
    children: [
      {
        path: '',
        redirectTo: 'panelControl',
        pathMatch: 'full',
      },
      {
        path: 'panelControl',
        component: PanelControl,
      },
      {
        path: 'gestionUsuario',
        component: GestionUsuarios,
      },
      {
        path: 'gestionMesas',
        component: GestionMesas,
      },
      {
        path: 'panelCocina',
        component: PanelCocina,
      },
      // {
      //   path: 'reportes',
      //   component: Reportes,
      // },
    ],
  },
];
