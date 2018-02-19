import React, { Component } from 'react';
import Pool from './components/Pool'
import HomePage from './components/HomePage'
import TestItem from './components/TestItem'

class App extends Component {
  renderComponent() {
    if (window.mainData) {
      return <HomePage />
    }
    if (window.test) {
      return <TestItem />
    }
    if (window.pool) {
      return <Pool />
    }
    return null;
  }

  render() {
    return (
      <div className="page">
        <div className="page-content">
          { this.renderComponent() }
          {/*"/pools/:poolId/tests/:deviceId/:testId" />*/}
        </div>
      </div>
    );
  }
}

export default App;
