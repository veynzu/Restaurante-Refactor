import { Routes} from '@angular/router';
import { Login } from './components/login/login'
import { Layout } from './components/layout/layout';
import { Dashboard } from './components/dashboard/dashboard';
import { dashboardRoutes } from './components/panel-control/dashboard.routes';
import { PanelControl } from './components/panel-control/panel-control';

export const routes: Routes = [

  {
    path:'',
    redirectTo:'login',
    pathMatch:'full'
  },
  {
    path:'login',
    component:Login
  },
  {
    path: '',
    component: Layout,
    children:[
      {
        path:'dashboard',
        children: dashboardRoutes,
      }
    ]
  }
];
