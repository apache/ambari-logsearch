import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable()
export class NavigationService {

  pendingNavigation = 0;

  constructor(private router: Router) {}

  private removePendingNavigation = () => {
    const currentPendingNavigation = this.pendingNavigation;
    this.pendingNavigation = currentPendingNavigation > 0 ? currentPendingNavigation - 1 : 0;
  }

  private addPendingNavigation = () => {
    this.pendingNavigation += 1;
  }

  private callRouterMethod(method, ...args) {
    this.addPendingNavigation();
    const promise: Promise<boolean> = this.router[method].apply(this.router, args);
    promise.then(this.removePendingNavigation, this.removePendingNavigation).catch(this.removePendingNavigation);
    return promise;
  }

  navigate(...args) {
    return this.callRouterMethod('navigate', ...args);
  }

  navigateByUrl(...args) {
    return this.callRouterMethod('navigateByUrl', ...args);
  }

}
