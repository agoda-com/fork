import React, {Component} from 'react';
import convertTime from './../utils/convertTime';
import paths from './../utils/paths';

export default class PoolsList extends Component {
    render() {
        return (
            <div className="content margin-top-20">
                <div className="title-common">{window.mainData.title}</div>

                <div key="summary" className="suite-item card">
                    <div className="title-common">Pool summary</div>
                    <div className="row full margin-bottom-20 bounded">
                        <div className="card-info">
                            <div className="text-sub-title-light">Passed</div>
                            <div className="card-info__content status-passed">{window.mainData.total_passed}</div>
                        </div>
                        <div className="card-info">
                            <div className="text-sub-title-light">Failed</div>
                            <div className="card-info__content status-failed">{window.mainData.total_failed}</div>
                        </div>
                        <div className="card-info">
                            <div className="text-sub-title-light">Ignored</div>
                            <div className="card-info__content status-ignored">{window.mainData.total_ignored}</div>
                        </div>
                        <div className="card-info">
                            <div className="text-sub-title-light">Total Duration</div>
                            <div
                                className="card-info__content">{convertTime(window.mainData.total_duration_millis)}</div>
                        </div>
                        {!window.mainData.pools.length > 0 && <div>
                            <div className="card-info">
                                <div className="text-sub-title-light">Average Duration</div>
                                <div
                                    className="card-info__content">{convertTime(window.mainData.average_duration_millis)}</div>
                            </div>
                            < div className="card-info">
                                <div className="text-sub-title-light">Min Duration</div>
                                <div
                                    className="card-info__content">{convertTime(window.mainData.min_duration_millis)}</div>
                            </div>
                            <div className="card-info">
                                <div className="text-sub-title-light">Max Duration</div>
                                <div
                                    className="card-info__content">{convertTime(window.mainData.max_duration_millis)}</div>
                            </div>
                        </div>}
                    </div>
                </div>

                <div className="title-common">Pools</div>

                {window.mainData.pools.map((pool) => {
                        return (
                            <div key={pool.id} className="suite-item card">
                                <a href={paths.fromIndexToPool(pool.id)} className="title-common with-arrow">
                                    Pool {pool.id}
                                </a>
                                <div className="row full margin-bottom-20 bounded">
                                    <div className="card-info">
                                        <div className="text-sub-title-light">Passed</div>
                                        <div className="card-info__content status-passed">{pool.passed_count}</div>
                                    </div>
                                    <div className="card-info">
                                        <div className="text-sub-title-light">Failed</div>
                                        <div className="card-info__content status-failed">{pool.failed_count}</div>
                                    </div>
                                    <div className="card-info">
                                        <div className="text-sub-title-light">Duration</div>
                                        <div className="card-info__content">{convertTime(pool.duration_millis)}</div>
                                    </div>
                                    <div className="card-info">
                                        <div className="text-sub-title-light">Devices</div>
                                        <div className="card-info__content">{pool.devices.length}</div>
                                    </div>
                                </div>
                            </div>
                        )
                    }
                )}
            </div>
        );
    }
}
