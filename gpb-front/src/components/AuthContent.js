import * as React from 'react';

import { request, setAuthHeader } from '../helpers/axios_helper';

export default class AuthContent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            data: []
        }
    };

    render() {
        return (
            <div className="row justify-content-md-center">
                <div className="col-4">
                </div>
            </div>
        );
      };
    }