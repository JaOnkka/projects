
 /** 
  * Creates a new vertex buffer and loads it full of the given data.
  * Preserves bound buffer.
  * 
  * @param {WebGLRenderingContext} gl  
  * @param {number[]} data
  * @param {number} usage
  * 
  * @returns {WebGlBuffer}
 */
function create_and_load_vertex_buffer(gl, data, usage) {
    let current_array_buf = gl.getParameter( gl.ARRAY_BUFFER_BINDING );

    let buf_id = gl.createBuffer();
    gl.bindBuffer( gl.ARRAY_BUFFER, buf_id );
    gl.bufferData( gl.ARRAY_BUFFER, new Float32Array(data), usage );
    
    gl.bindBuffer( gl.ARRAY_BUFFER, current_array_buf );

    return buf_id;
}

/**
 * 
 * @param {WebGLRenderingContext} gl
 * @param {WebGLShader} shader_id 
 */
function assert_shader_compiled_correctly( gl, shader_id ) {
    if( !gl.getShaderParameter( shader_id, gl.COMPILE_STATUS ) ) {
        let err = gl.getShaderInfoLog( shader_id );
        let shader_kind = gl.getShaderParameter( shader_id, gl.SHADER_TYPE );
        let shader_kind_name = 
            shader_kind == gl.VERTEX_SHADER ? 'vertex shader' :
            shader_kind == gl.FRAGMENT_SHADER ? 'fragment shader' :
            'unknown shader'; 

        throw new Error( 'Compile error in ' + shader_kind_name + ':\n' + err );
    }

    return true;
}

/**
 * Creates a new shader program, creates and attaches vertex and fragment shaders 
 * from the given sources, links the resulting program, and returns it. 
 * 
 * @param {WebGLRenderingContext} gl
 * @param {string} v_shader_src 
 * @param {string} f_shader_src 
 * 
 * @returns {WebGLProgram}
 */
function create_compile_and_link_program( gl, v_shader_src, f_shader_src ) {
    let program = gl.createProgram()
    
    let v_shader = gl.createShader( gl.VERTEX_SHADER );
    gl.shaderSource( v_shader, v_shader_src );
    gl.compileShader( v_shader );
    assert_shader_compiled_correctly( gl, v_shader );

    let f_shader = gl.createShader( gl.FRAGMENT_SHADER );
    gl.shaderSource( f_shader, f_shader_src );
    gl.compileShader( f_shader );
    assert_shader_compiled_correctly( gl, f_shader );

    gl.attachShader( program, v_shader );
    gl.attachShader( program, f_shader );
    gl.linkProgram( program );

    if( !gl.getProgramParameter( program, gl.LINK_STATUS ) ) {
        let err = gl.getProgramInfoLog( program );
        throw new Error( 'Link error in shader program:\n' + err );
    }

    return program;
}

/**
 * 
 * @param {WebGLRenderingContext} gl 
 * @param {WebGLProgram} program 
 */
function delete_program_and_attached_shaders( gl, program ) {
    let shaders = gl.getAttachedShaders( program );
    gl.deleteProgram( program );

    shaders.forEach( function( shader ) { gl.deleteShader( shader ); } );
}

/**
 * Sets the buffer for a given vertex attribute name. 
 * 
 * @param {WebGLRenderingContext} gl 
 * @param {WebGLProgram} program 
 * @param {string} attrib_name 
 * @param {WebGLBuffer} buffer
 * @param {number} n_components 
 * @param {number} gl_type 
 * @param {number} stride 
 * @param {number} offset
 */
function set_vertex_attrib_to_buffer( 
    gl, program, attrib_name, buffer, n_components, gl_type, normalize, stride, offset ) 
{
    let attr_loc = gl.getAttribLocation( program, attrib_name );
    
    if ( attr_loc == - 1 ) { 
        throw new Error( 'either no attribute named "' + attrib_name + 
            '" in program or attribute name is reserved/built-in.' ) 
    } 

    let err = gl.getError()
    if ( err != 0 ) {
        throw new Error( 'invalid program. Error: ' + err );
    }

    let current_array_buf = gl.getParameter( gl.ARRAY_BUFFER_BINDING );

    gl.bindBuffer( gl.ARRAY_BUFFER, buffer );
    gl.enableVertexAttribArray( attr_loc );
    gl.vertexAttribPointer( attr_loc, n_components, gl_type, normalize, stride, offset );
    //gl.enableVertexAttribArray( attr_loc );

    gl.bindBuffer( gl.ARRAY_BUFFER, current_array_buf );
}

/**
 * Set global parameters such as "clear color". 
 * @param {WebGLRenderingContext} gl 
 */
function set_render_params( gl ) {
    // gl.clearColor( 0.0, 0.0, 0.0, 1 );
    gl.clearColor( 0.5, 0.8, 1.0, 1.0 );

    gl.enable( gl.DEPTH_TEST );
    gl.enable( gl.BLEND );

    gl.depthMask( true );
    gl.depthFunc( gl.LEQUAL );

    gl.blendFunc( gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA );

    // gl.viewport( 0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight );
}

/**
 * Sets uniform data for a row-major matrix4
 * 
 * @param {WebGLRenderingContext} gl 
 * @param {WebGLProgram} program
 * @param {string} name 
 * @param {number[]} data 
 */
function set_uniform_matrix4( gl, program, name, data ) {
    //let old_prog = gl.getParameter( gl.CURRENT_PROGRAM );
    //gl.useProgram( program );

    const loc = gl.getUniformLocation( program, name );
    gl.uniformMatrix4fv( loc, true, data );

    //gl.useProgram( old_prog );
}

 /** 
  * Creates a new index buffer and loads it full of the given data.
  * Preserves bound buffer.
  * 
  * @param {WebGLRenderingContext} gl  
  * @param {number[]} data
  * @param {number} usage
  * 
  * @returns {WebGlBuffer}
 */
function create_and_load_elements_buffer(gl, data, usage) {
    let current_buf = gl.getParameter( gl.ELEMENT_ARRAY_BUFFER_BINDING );
    
    let buf_id = gl.createBuffer();
    gl.bindBuffer( gl.ELEMENT_ARRAY_BUFFER, buf_id );
    gl.bufferData( gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(data), usage );

    gl.bindBuffer( gl.ELEMENT_ARRAY_BUFFER, current_buf );
    
    return buf_id;
}


/**
 * Set the built-in shader uniform to texture unit 0.
 * (this isn't strictly necessary, but would if we wanted to use multi-texturing)
 * @param {WebGLRenderingContext} gl 
 */
function bind_texture_samplers( gl, program, sampler_name ) {
    const old_prog = gl.getParameter( gl.CURRENT_PROGRAM );
    gl.useProgram( program );

    const loc = gl.getUniformLocation( program, sampler_name );
    gl.uniform1i( loc, 0 );

    gl.useProgram( old_prog );
}

/**
 * Sets uniform data for a 3 component vector.
 * @param {WebGLRenderingContext} gl 
 * @param {WebGLProgram} program 
 * @param {string} name 
 */
function set_uniform_vec3( gl, program, name, x, y, z ) {
    // let old_prog = gl.getParameter( gl.CURRENT_PROGRAM );
    // gl.useProgram( program );

    const loc = gl.getUniformLocation( program, name );

    gl.uniform3f( loc, x, y, z );

    // gl.useProgram( old_prog );
}

/**
 * Set a single float value
 * @param {*} gl 
 * @param {*} program 
 * @param {*} name 
 * @param {*} x 
 */
function set_uniform_scalar( gl, program, name, x ){
    // let old_prog = gl.getParameter( gl.CURRENT_PROGRAM );
    // gl.useProgram( program );

    const loc = gl.getUniformLocation( program, name );
    gl.uniform1f( loc, x );

    // gl.useProgram( old_prog );
}

/**
 * Sets uniform data for an array of 3 component vectors.
 * @param {WebGLRenderingContext} gl 
 * @param {WebGLProgram} program 
 * @param {string} name 
 * @param {number[]} data must be flattened
 */
function set_uniform_vec3_array( gl, program, name, data ) {
    // let old_prog = gl.getParameter( gl.CURRENT_PROGRAM );
    // gl.useProgram( program );

    const loc = gl.getUniformLocation( program, name );

    gl.uniform3fv( loc, data )

    // gl.useProgram( old_prog );
}

function set_uniform_int( gl, program, name, data ) {
    // let old_prog = gl.getParameter( gl.CURRENT_PROGRAM );
    // gl.useProgram( program );

    const loc = gl.getUniformLocation( program, name );

    gl.uniform1i( loc, data );

    // gl.useProgram( old_prog );
}

//this just makes a flat heightmap of height = 1
//for our starting plateau
function diamondSquareA(heightMap, scale, roughness, minHeight, maxHeight, centerRow, centerCol) {
    console.log(heightMap);
    scale -= 1;
    console.log("Scale " + scale);
    topleft = heightMap[0][0] = 1;
    topright = heightMap[0][scale] = 1;
    bottomleft = heightMap[scale][0] = 1;
    bottomright = heightMap[scale][scale] = 1;

    let startRow = 0;
    let startCol = 0;
    let endRow = scale;
    let endCol = scale;

    middle = heightMap[centerRow][centerCol] = (heightMap[startRow][startCol] + heightMap[endRow][startCol] + heightMap[startRow][endCol] + heightMap[endRow][endCol] / 4 + (Math.random(minHeight,maxHeight) * roughness));

    heightMap[startRow][centerCol] = calculateAverage(heightMap, startRow, centerCol, scale, roughness,minHeight,maxHeight);

    // South
    heightMap[endRow][centerCol] = calculateAverage(heightMap, endRow, centerCol, scale, roughness,minHeight,maxHeight);

    // East
    heightMap[centerRow][endCol] = calculateAverage(heightMap, centerRow, endCol, scale, roughness,minHeight,maxHeight);

    // West
    heightMap[centerRow][startCol] = calculateAverage(heightMap, centerRow, startCol, scale, roughness,minHeight,maxHeight)
    recursiveDiamondStep(heightMap, 0, 0, scale, scale, scale, roughness,minHeight,maxHeight);


}
//this one builds more random heightmaps for the outer ones
function diamondSquareB(heightMap, scale, roughness, minHeight, maxHeight, centerRow, centerCol) {
    console.log(heightMap);
    scale -= 1;
    
    random_offset = (Math.random(minHeight,maxHeight) * roughness);
    

    topleft = heightMap[0][0] = (Math.random(minHeight,maxHeight) * roughness);
    topright = heightMap[0][scale] = (Math.random(minHeight,maxHeight) * roughness);
    bottomleft = heightMap[scale][0] = (Math.random(minHeight,maxHeight) * roughness);
    bottomright = heightMap[scale][scale] = (Math.random(minHeight,maxHeight) * roughness);
    random_offset = (Math.random(minHeight,maxHeight) * roughness)
    middle = heightMap[centerRow][centerCol] = (heightMap[0][0] + heightMap[scale][0] + heightMap[0][scale] + heightMap[scale][scale] / 4 + random_offset);


    recursiveDiamondStep(heightMap, 0, 0, scale, scale, scale, roughness,minHeight,maxHeight);


}
function recursiveDiamondStep(heightMap, startRow, startCol, endRow, endCol, scale, roughness,minHeight,maxHeight) {
    if (scale <= 1) {
        return; //algorithm ends when radius is 1
    }

    let middleRow = (startRow + endRow) / 2;
    let middleCol = (startCol + endCol) / 2;

    //middle
    heightMap[middleRow][middleCol] = calculateAverage(heightMap, middleRow, middleCol, scale, roughness);

    //square step for edges
    squareStep(heightMap, startRow, startCol, endRow, endCol, scale, roughness,minHeight,maxHeight);

    //recursive diamond step
    recursiveDiamondStep(heightMap, startRow, startCol, middleRow, middleCol, scale / 2, roughness,minHeight,maxHeight);
    recursiveDiamondStep(heightMap, startRow, middleCol, middleRow, endCol, scale / 2, roughness,minHeight,maxHeight);
    recursiveDiamondStep(heightMap, middleRow, startCol, endRow, middleCol, scale / 2, roughness,minHeight,maxHeight);
    recursiveDiamondStep(heightMap, middleRow, middleCol, endRow, endCol, scale / 2, roughness,minHeight,maxHeight);
}


function squareStep(heightMap, startRow, startCol, endRow, endCol, scale, roughness,minHeight,maxHeight) {
    let middleRow = (startRow + endRow) / 2;
    let middleCol = (startCol + endCol) / 2;

    //top edge
    heightMap[startRow][middleCol] = calculateAverage(heightMap, startRow, middleCol, scale, roughness, minHeight, maxHeight);

    //bottom edge
    heightMap[endRow][middleCol] = calculateAverage(heightMap, endRow, middleCol, scale, roughness, minHeight, maxHeight);

    //left edge
    heightMap[middleRow][startCol] = calculateAverage(heightMap, middleRow, startCol, scale, roughness, minHeight, maxHeight);

    //right edge
    heightMap[middleRow][endCol] = calculateAverage(heightMap, middleRow, endCol, scale, roughness, minHeight, maxHeight);
}

function calculateAverage(heightMap, row, col, scale, roughness, minHeight, maxHeight) {
    const half = Math.floor(scale / 2);
    const count = 4;

    let sum = 0;
    let validNeighbors = 0;

    // North
    if (row - half >= 0) {
        sum += heightMap[row - half][col];
        validNeighbors++;
    }

    // South
    if (row + half < heightMap.length) {
        sum += heightMap[row + half][col];
        validNeighbors++;
    }

    // East
    if (col + half < heightMap[0].length) {
        sum += heightMap[row][col + half];
        validNeighbors++;
    }

    // West
    if (col - half >= 0) {
        sum += heightMap[row][col - half];
        validNeighbors++;
    }

    let total = sum / validNeighbors + (Math.random(minHeight,maxHeight) * roughness);
    if(total > maxHeight){
        return maxHeight;
    }
    else{
        return total;
    }
}
