/*
 * Copyright 2018 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React from 'react';
import PropTypes from 'prop-types';

import { Header, Button, Sidebar, Menu, Icon, Segment, Image, Input } from 'semantic-ui-react';

import { compose, withState } from 'recompose';
import { connect } from 'react-redux';
import { LineContainer, LineTo } from 'components/LineTo';
import { mapObject } from 'lib/treeUtils';
import { withPipeline } from './withPipeline';
import { actionCreators } from './redux';

import PipelineElement from './PipelineElement';
import PipelineElementSettings from './PipelineElementSettings';
import PaletteElement from './PaletteElement';
import { AddElementWizard } from './AddElementToPipeline';
import { ElementPalette } from './ElementPalette';

import lineElementCreators from './pipelineLineElementCreators';

const HORIZONTAL_SPACING = 150;
const VERTICAL_SPACING = 50;
const HORIZONTAL_START_PX = 50;
const VERTICAL_START_PX = 50;
const COMMON_ELEMENT_STYLE = {
  position: 'absolute',
};

const withPaletteOpen = withState('isPaletteOpen', 'setPaletteOpen', false);
const withRecycleBinOpen = withState('isRecycleBinOpen', 'setRecycleBinOpen', false);

const PipelineEditor = ({
  pipelineId,
  pipeline,
  pendingElementIdToDelete,
  layoutInformation,
  isPaletteOpen,
  setPaletteOpen,

  isRecycleBinOpen,
  setRecycleBinOpen,

  elementsByCategory,
}) => {
  const togglePaletteOpen = () => setPaletteOpen(!isPaletteOpen);
  const toggleRecycleBin = () => setRecycleBinOpen(!isRecycleBinOpen);

  const elementStyles = mapObject(layoutInformation, l => ({
    ...COMMON_ELEMENT_STYLE,
    top: `${VERTICAL_START_PX + l.verticalPos * VERTICAL_SPACING}px`,
    left: `${HORIZONTAL_START_PX + l.horizontalPos * HORIZONTAL_SPACING}px`,
  }));

  const handleKeyPress = (e) => {
    const k = e.key;
    switch (k) {
      case 'x':
        toggleRecycleBin();
        break;
      case 'e':
        togglePaletteOpen();
        break;
    }
  };

  return (
    <Sidebar.Pushable as={Segment} tabIndex={0} onKeyPress={handleKeyPress}>
      <Sidebar
        as={Menu}
        animation="push"
        width="thin"
        visible={isPaletteOpen}
        icon="labeled"
        vertical
      >
        {Object.entries(elementsByCategory).map(k => (
          <Menu.Item key={k[0].toString()}>
            <Menu.Header header>{k[0]}</Menu.Header>
            <Menu.Menu>{k[1].map(e => <PaletteElement key={e.type} element={e} />)}</Menu.Menu>
          </Menu.Item>
        ))}
      </Sidebar>
      <Sidebar.Pusher>
        <Sidebar.Pushable as={Segment}>
          <Sidebar as={Menu} animation="push" direction="bottom" visible={isRecycleBinOpen}>
            <Menu.Item name="home">
              <Icon name="home" />
              Home
            </Menu.Item>
            <Menu.Item name="gamepad">
              <Icon name="gamepad" />
              Games
            </Menu.Item>
            <Menu.Item name="camera">
              <Icon name="camera" />
              Channels
            </Menu.Item>
          </Sidebar>
          <Sidebar.Pusher>
            <div className="Pipeline-editor">
              <LineContainer
                lineContextId={`pipeline-lines-${pipelineId}`}
                lineElementCreators={lineElementCreators}
              >
                <Header as="h4">Pipeline Editor {pipelineId}</Header>
                <Button onClick={togglePaletteOpen}>Elements</Button>
                <Button onClick={toggleRecycleBin}>Recycle Bin</Button>
                {pipeline.elements.add.map(e => (
                  <div key={e.id} id={e.id} style={elementStyles[e.id]}>
                    <PipelineElement pipelineId={pipelineId} elementId={e.id} />
                  </div>
                ))}
                {pipeline.links.add
                  .map(l => ({ ...l, lineId: `${l.from}-${l.to}` }))
                  .map(l => (
                    <LineTo
                      lineId={l.lineId}
                      key={l.lineId}
                      fromId={l.from}
                      toId={l.to}
                      lineType="curve"
                    />
                  ))}
              </LineContainer>
            </div>
          </Sidebar.Pusher>
        </Sidebar.Pushable>
      </Sidebar.Pusher>
    </Sidebar.Pushable>
  );
};

PipelineEditor.propTypes = {
  pipelineId: PropTypes.string.isRequired,
  pipeline: PropTypes.object.isRequired,
  asTree: PropTypes.object.isRequired,
  layoutInformation: PropTypes.object.isRequired,
  elementsByCategory: PropTypes.object.isRequired,

  // withPaletteOpen
  isPaletteOpen: PropTypes.bool.isRequired,
  setPaletteOpen: PropTypes.func.isRequired,

  // withRecycleBinOpen
  isRecycleBinOpen: PropTypes.bool.isRequired,
  setRecycleBinOpen: PropTypes.func.isRequired,
};

export default compose(
  connect(
    state => ({
      elementsByCategory: state.elements.byCategory || {},
    }),
    {
      // actions
    },
  ),
  withPipeline(),
  withPaletteOpen,
  withRecycleBinOpen,
)(PipelineEditor);
