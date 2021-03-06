/*
 * The MIT License
 *  
 *  Copyright (c) 2015, CloudBees, Inc.
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package it.dockins.dockerslaves;

import it.dockins.dockerslaves.api.OneShotComputer;
import hudson.slaves.ComputerLauncher;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * A computer on which a specific build will occur
 */
public class DockerComputer extends OneShotComputer {

    private final DockerSlave slave;

    private final DockerProvisionerFactory provisionerFactory;

    private DockerProvisioner provisioner;

    public DockerComputer(DockerSlave slave, DockerProvisionerFactory provisionerFactory) {
        super(slave);
        this.provisionerFactory = provisionerFactory;
        this.slave = slave;
    }

    @Override
    public DockerSlave getNode() {
        return slave;
    }

    /*
     * Create a container provisioner to setup this Jenkins "computer" (aka executor)
     *
     */
    public DockerProvisioner createProvisioner() throws IOException, InterruptedException {
        provisioner = provisionerFactory.createProvisioner(getListener());
        return provisioner;
    }

    @Override
    protected void terminate() {
        LOGGER.info("Stopping Docker Slave after build completion");
        setAcceptingTasks(false);
        try {
            provisioner.clean();
        } catch (InterruptedException e) {
            e.printStackTrace(); //FIXME
        } catch (IOException e) {
            e.printStackTrace(); //FIXME
        }
        super.terminate();
    }

    public DockerProvisioner getProvisioner() {
        return provisioner;
    }

    private static final Logger LOGGER = Logger.getLogger(DockerComputer.class.getName());

    public ComputerLauncher createComputerLauncher() {
        return new DockerComputerLauncher();
    }
}
