import React from 'react'
import { Route, Switch } from 'react-router-dom'

import Header from './profile/Header'
import InfoPanel from './profile/InfoPanel'
import Chat from './chat/Chat'
import ComingSoon from './util/ComingSoon'

import Grid from 'react-bootstrap/lib/Grid'
import Row from 'react-bootstrap/lib/Row'
import Col from 'react-bootstrap/lib/Col'

export default class App extends React.Component {
    render() {
        return (
            <div>
                <Header />
                <Grid>
                    <Row>
                        <Col xs={12} md={8}>
                            <Switch>
                                <Route exact path='/' component={Chat} />
                                <Route path='/chat' component={Chat} />
                                <Route path='/coming-soon' component={ComingSoon} />
                            </Switch>
                        </Col>
                        <Col xs={4} md={4}>
                            <InfoPanel />
                        </Col>
                    </Row>
                </Grid>
            </div>
        )
    }
}
