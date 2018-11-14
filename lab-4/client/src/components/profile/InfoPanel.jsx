import React from 'react'

import Panel from 'react-bootstrap/lib/Panel'
import ListGroup from 'react-bootstrap/lib/ListGroup'
import ListGroupItem from 'react-bootstrap/lib/ListGroupItem'
import Label from 'react-bootstrap/lib/Label'

export default class InfoPanel extends React.Component {
    render() {
        return (
            <div>
                <Panel>
                    <Panel.Heading>Профиль студента</Panel.Heading>
                    <ListGroup>
                        <ListGroupItem><b>Имя: </b>Анастасия Шадрина</ListGroupItem>
                        <ListGroupItem><b>Группа: </b>16206</ListGroupItem>
                    </ListGroup>
                </Panel>
                <Panel bsStyle="warning">
                    <Panel.Heading>
                        Студентов онлайн{' '}
                        <Label>1</Label>
                    </Panel.Heading>
                </Panel>
            </div>
        )
    }
}
